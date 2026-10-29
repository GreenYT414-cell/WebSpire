const express = require('express');
const cors = require('cors');
const { initDB, readData, writeData, hashPassword, generateToken, verifyToken } = require('./db');

const app = express();
const PORT = process.env.PORT || 5000;

app.use(cors());
app.use(express.json());

// Khởi tạo Database nếu chưa tồn tại
initDB();

// Middleware xác thực phiên chơi và token người chơi
function authenticateToken(req, res, next) {
    const authHeader = req.headers['authorization'];
    if (!authHeader) {
        // Cho phép chế độ khách ngoại tuyến
        const userId = req.body.userId || req.params.userId;
        if (userId && (userId.startsWith('GUEST_') || userId === 'guest')) {
            req.user = { userId, isGuest: true };
            return next();
        }
        return res.status(401).json({ success: false, message: "Yêu cầu đăng nhập để thực hiện thao tác này!" });
    }

    const token = authHeader.startsWith('Bearer ') ? authHeader.slice(7).trim() : authHeader.trim();
    const verified = verifyToken(token);
    if (!verified) {
        return res.status(403).json({ success: false, message: "Phiên đăng nhập không hợp lệ hoặc đã hết hạn!" });
    }

    // Bảo vệ chống giả mạo danh tính: token phải khớp với userId được gửi
    if (!verified.isGuest && req.body.userId && verified.userId !== req.body.userId) {
        return res.status(403).json({ success: false, message: "Không có quyền can thiệp vào dữ liệu tài khoản khác!" });
    }

    req.user = verified;
    next();
}

// 1. Kiểm tra trạng thái máy chủ (Health check)
app.get('/api/health', (req, res) => {
    res.json({
        status: "ONLINE",
        game: "WebSpire: The Tower",
        version: "1.0.0",
        timestamp: new Date().toISOString()
    });
});

// 2. Đăng ký tài khoản mới
app.post('/api/auth/register', (req, res) => {
    const { username, email, password } = req.body;

    if (!username || !password) {
        return res.status(400).json({ success: false, message: "Tên đăng nhập và mật khẩu không được để trống!" });
    }

    const data = readData();
    const existing = data.users.find(u => u.username.toLowerCase() === username.toLowerCase() || (email && u.email.toLowerCase() === email.toLowerCase()));
    if (existing) {
        return res.status(400).json({ success: false, message: "Tài khoản hoặc Email đã tồn tại!" });
    }

    const newUser = {
        id: "USER_" + (1000 + data.users.length + 1),
        username: username.trim(),
        email: email ? email.trim() : username.trim() + "@gamehub.com",
        passwordHash: hashPassword(password),
        createdAt: new Date().toISOString()
    };

    data.users.push(newUser);
    writeData(data);

    const token = generateToken(newUser.id);
    console.log(`[AUTH] Người chơi mới đăng ký: ${newUser.username} (${newUser.id})`);

    res.json({
        success: true,
        message: "Đăng ký tài khoản thành công!",
        token: token,
        user: {
            id: newUser.id,
            username: newUser.username,
            email: newUser.email
        }
    });
});

// 3. Đăng nhập tài khoản
app.post('/api/auth/login', (req, res) => {
    const { username, password } = req.body;

    if (!username || !password) {
        return res.status(400).json({ success: false, message: "Vui lòng nhập tài khoản và mật khẩu!" });
    }

    const data = readData();
    const hash = hashPassword(password);
    const user = data.users.find(u => 
        (u.username.toLowerCase() === username.toLowerCase() || u.email.toLowerCase() === username.toLowerCase()) 
        && u.passwordHash === hash
    );

    if (!user) {
        return res.status(401).json({ success: false, message: "Sai tên đăng nhập hoặc mật khẩu!" });
    }

    const token = generateToken(user.id);
    console.log(`[AUTH] Người chơi đăng nhập thành công: ${user.username} (${user.id})`);

    res.json({
        success: true,
        message: "Đăng nhập thành công!",
        token: token,
        user: {
            id: user.id,
            username: user.username,
            email: user.email
        }
    });
});

// 4. Lưu tiến trình chơi (Cloud Save Run Profile)
app.post('/api/game/profile', authenticateToken, (req, res) => {
    const { userId, currentFloor, currentHp, maxHp, gold, masterDeck, relics } = req.body;

    if (!userId) {
        return res.status(400).json({ success: false, message: "Thiếu userId khi lưu tiến trình!" });
    }

    const data = readData();
    const profile = {
        userId,
        currentFloor: currentFloor || 1,
        currentHp: currentHp || 80,
        maxHp: maxHp || 80,
        gold: gold || 0,
        masterDeck: Array.isArray(masterDeck) ? masterDeck : [],
        relics: Array.isArray(relics) ? relics : [],
        updatedAt: new Date().toISOString()
    };

    data.profiles[userId] = profile;
    writeData(data);

    console.log(`[CLOUD SAVE] Đã đồng bộ tiến trình cho ${userId}: Tầng ${profile.currentFloor}, ${profile.gold} Vàng, ${profile.masterDeck.length} Bài`);

    res.json({
        success: true,
        message: "Lưu tiến trình lên Cloud Server GameHub thành công!",
        profile
    });
});

// 5. Nạp lại tiến trình đã lưu từ Cloud Server
app.get('/api/game/profile/:userId', (req, res) => {
    const { userId } = req.params;
    const data = readData();

    const profile = data.profiles[userId];
    if (!profile) {
        return res.status(404).json({ success: false, message: "Không tìm thấy bản lưu tiến trình nào trên máy chủ!" });
    }

    res.json({
        success: true,
        message: "Tải tiến trình từ Cloud thành công!",
        profile
    });
});

// 6. Lấy bảng xếp hạng (Leaderboard)
app.get('/api/game/leaderboard', (req, res) => {
    const data = readData();
    const sorted = [...data.leaderboards].sort((a, b) => (b.score || 0) - (a.score || 0)).slice(0, 20);

    res.json({
        success: true,
        leaderboard: sorted
    });
});

// 7. Gửi kết quả lượt chơi lên bảng xếp hạng khi Thắng / Thua
app.post('/api/game/leaderboard', authenticateToken, (req, res) => {
    const { userId, username, floorReached, score, enemiesKilled, bossesKilled, totalDamageDealt } = req.body;

    if (!userId || !username) {
        return res.status(400).json({ success: false, message: "Thiếu thông tin người chơi!" });
    }

    const calculatedScore = score || (floorReached * 150 + (enemiesKilled || 0) * 50 + (bossesKilled || 0) * 300);

    const record = {
        userId,
        username,
        floorReached: floorReached || 1,
        score: calculatedScore,
        enemiesKilled: enemiesKilled || 0,
        bossesKilled: bossesKilled || 0,
        totalDamageDealt: totalDamageDealt || 0,
        completedAt: new Date().toISOString()
    };

    const data = readData();
    data.leaderboards.push(record);
    writeData(data);

    console.log(`[LEADERBOARD] Đã ghi nhận kỷ lục mới: ${username} - ${calculatedScore} Điểm (Tầng ${floorReached})`);

    res.json({
        success: true,
        message: "Đã cập nhật kỷ lục lên Bảng Xếp Hạng GameHub!",
        record
    });
});

// 8. Đổi / Nhận Voucher giảm giá GameHub
app.get('/api/game/coupons/:floor', (req, res) => {
    const floor = parseInt(req.params.floor) || 0;
    const data = readData();

    // Tìm coupon phù hợp với mốc tầng đạt được
    const eligible = data.coupons
        .filter(c => floor >= c.floor)
        .sort((a, b) => b.floor - a.floor);

    if (eligible.length > 0) {
        res.json({
            success: true,
            message: `Chúc mừng bạn đã mở khóa voucher ${eligible[0].code}!`,
            coupon: eligible[0]
        });
    } else {
        res.status(404).json({
            success: false,
            message: "Bạn cần vượt qua ít nhất Tầng 5 để nhận Voucher giảm giá GameHub!"
        });
    }
});

// 9. Danh mục sản phẩm GameHub (Categories)
app.get('/api/categories', (req, res) => {
    const data = readData();
    res.json({
        success: true,
        categories: data.categories || []
    });
});

// 10. Danh sách sản phẩm GameHub (Products)
app.get('/api/products', (req, res) => {
    const { category, search } = req.query;
    const data = readData();
    let products = data.products || [];

    if (category && category !== 'all') {
        products = products.filter(p => p.category === category);
    }

    if (search) {
        const query = search.toLowerCase();
        products = products.filter(p => 
            p.name.toLowerCase().includes(query) || 
            (p.description && p.description.toLowerCase().includes(query))
        );
    }

    res.json({
        success: true,
        total: products.length,
        products
    });
});

// 11. Chi tiết sản phẩm (Product detail)
app.get('/api/products/:id', (req, res) => {
    const { id } = req.params;
    const data = readData();
    const product = (data.products || []).find(p => p.id === id);

    if (!product) {
        return res.status(404).json({ success: false, message: "Không tìm thấy sản phẩm!" });
    }

    res.json({
        success: true,
        product
    });
});

// 12. Danh sách tất cả Voucher có thể nhận từ WebSpire
app.get('/api/coupons', (req, res) => {
    const data = readData();
    res.json({
        success: true,
        coupons: data.coupons || []
    });
});

// 13. Kiểm tra mã giảm giá (Validate Coupon)
app.get('/api/coupons/validate/:code', (req, res) => {
    const code = (req.params.code || '').trim().toUpperCase();
    const data = readData();
    const coupon = (data.coupons || []).find(c => c.code.toUpperCase() === code);

    if (!coupon) {
        return res.status(404).json({
            success: false,
            message: "Mã giảm giá không hợp lệ hoặc đã hết hạn!"
        });
    }

    res.json({
        success: true,
        message: `Mã ${coupon.code} hợp lệ (-${coupon.discountPercent}%)`,
        coupon
    });
});

// 14. Kiểm tra các voucher đã mở khóa của một người dùng theo tiến trình chơi
app.get('/api/user/unlocked-coupons/:userId', (req, res) => {
    const { userId } = req.params;
    const data = readData();
    const profile = data.profiles[userId];
    const floor = profile ? profile.currentFloor : 1;

    const unlocked = (data.coupons || []).filter(c => floor >= c.floor);
    const locked = (data.coupons || []).filter(c => floor < c.floor);

    res.json({
        success: true,
        currentFloor: floor,
        unlockedCoupons: unlocked,
        lockedCoupons: locked
    });
});

// 15. Tạo đơn hàng mới (Create Order)
app.post('/api/orders', (req, res) => {
    const { userId, customerName, phone, address, items, couponCode, paymentMethod } = req.body;

    if (!items || items.length === 0) {
        return res.status(400).json({ success: false, message: "Giỏ hàng không có sản phẩm nào!" });
    }

    const data = readData();
    let subtotal = 0;

    // Tính tổng tiền dựa trên giá trong database để tránh gian lận
    const orderItems = items.map(item => {
        const prod = (data.products || []).find(p => p.id === item.productId || p.id === item.id);
        const price = prod ? prod.price : item.price;
        const qty = item.quantity || 1;
        subtotal += price * qty;
        return {
            productId: prod ? prod.id : item.id,
            name: prod ? prod.name : item.name,
            price,
            quantity: qty,
            totalPrice: price * qty
        };
    });

    let discountPercent = 0;
    let appliedCoupon = null;

    if (couponCode) {
        const found = (data.coupons || []).find(c => c.code.toUpperCase() === couponCode.trim().toUpperCase());
        if (found) {
            discountPercent = found.discountPercent;
            appliedCoupon = found.code;
        }
    }

    const discountAmount = Math.round((subtotal * discountPercent) / 100);
    const finalTotal = subtotal - discountAmount;

    const newOrder = {
        id: "ORD_" + Date.now(),
        userId: userId || "GUEST",
        customerName: customerName || "Khách Hàng GameHub",
        phone: phone || "",
        address: address || "",
        items: orderItems,
        subtotal,
        discountPercent,
        discountAmount,
        appliedCoupon,
        finalTotal,
        paymentMethod: paymentMethod || "COD",
        status: "SUCCESS",
        createdAt: new Date().toISOString()
    };

    data.orders = data.orders || [];
    data.orders.push(newOrder);
    writeData(data);

    console.log(`[ORDER] Đơn hàng mới: ${newOrder.id} - Tổng tiền: ${finalTotal.toLocaleString('vi-VN')} VNĐ (Giảm: ${discountPercent}%)`);

    res.json({
        success: true,
        message: "Đặt hàng thành công!",
        order: newOrder
    });
});

// 16. Lấy lịch sử đơn hàng của người dùng
app.get('/api/orders/user/:userId', (req, res) => {
    const { userId } = req.params;
    const data = readData();
    const userOrders = (data.orders || []).filter(o => o.userId === userId);

    res.json({
        success: true,
        orders: userOrders
    });
});

app.listen(PORT, () => {
    console.log(`===================================================`);
    console.log(`⚔ WebSpire & GameHub API Server đang lắng nghe tại: http://localhost:${PORT}`);
    console.log(`  - Health Check:    http://localhost:${PORT}/api/health`);
    console.log(`  - Auth API:        http://localhost:${PORT}/api/auth/login`);
    console.log(`  - Products Store:  http://localhost:${PORT}/api/products`);
    console.log(`  - Categories:      http://localhost:${PORT}/api/categories`);
    console.log(`  - Leaderboard:     http://localhost:${PORT}/api/game/leaderboard`);
    console.log(`  - WebSpire Coupons:http://localhost:${PORT}/api/coupons`);
    console.log(`===================================================`);
});
