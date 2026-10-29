const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const DATA_DIR = path.join(__dirname, 'data');
const DB_FILE = path.join(DATA_DIR, 'database.json');

// Khởi tạo thư mục và database mặc định
function initDB() {
    if (!fs.existsSync(DATA_DIR)) {
        fs.mkdirSync(DATA_DIR, { recursive: true });
    }

    if (!fs.existsSync(DB_FILE)) {
        const defaultData = {
            users: [
                {
                    id: "USER_1001",
                    username: "HiepSiBongDem",
                    email: "player@gamehub.com",
                    passwordHash: hashPassword("123456"),
                    createdAt: new Date().toISOString()
                }
            ],
            profiles: {
                "USER_1001": {
                    userId: "USER_1001",
                    currentFloor: 5,
                    currentHp: 68,
                    maxHp: 80,
                    gold: 145,
                    masterDeck: ["Cú Nện", "Chém", "Chém", "Phòng Thủ", "Phòng Thủ", "Sóng Xung Kích"],
                    relics: ["Máu Thiêu Đốt", "Mỏ Neo Thép"],
                    updatedAt: new Date().toISOString()
                }
            },
            leaderboards: [
                {
                    userId: "USER_1001",
                    username: "HiepSiBongDem",
                    floorReached: 15,
                    score: 2850,
                    enemiesKilled: 18,
                    bossesKilled: 2,
                    totalDamageDealt: 1420,
                    completedAt: new Date(Date.now() - 3600000 * 24).toISOString()
                },
                {
                    userId: "USER_1002",
                    username: "PhuThuyAoDo",
                    floorReached: 12,
                    score: 2100,
                    enemiesKilled: 14,
                    bossesKilled: 1,
                    totalDamageDealt: 1180,
                    completedAt: new Date(Date.now() - 3600000 * 48).toISOString()
                },
                {
                    userId: "USER_1003",
                    username: "SatThuBongMa",
                    floorReached: 8,
                    score: 1350,
                    enemiesKilled: 9,
                    bossesKilled: 1,
                    totalDamageDealt: 750,
                    completedAt: new Date(Date.now() - 3600000 * 72).toISOString()
                }
            ],
            coupons: [
                { code: "WEBSPIRE5", floor: 5, discountPercent: 10, description: "Voucher giảm 10% tại sàn GameHub khi vượt qua Tầng 5!" },
                { code: "WEBSPIRE8", floor: 8, discountPercent: 15, description: "Voucher giảm 15% tại sàn GameHub khi hạ gục Hộ Vệ Tháp Tầng 8!" },
                { code: "WEBSPIRE15", floor: 15, discountPercent: 25, description: "Voucher VIP giảm 25% tại sàn GameHub khi hoàn thành Tầng 15!" }
            ],
            categories: [
                { id: "all", name: "Tất cả" },
                { id: "games", name: "Game Bản Quyền" },
                { id: "roguelike", name: "Thẻ Bài & Roguelike" },
                { id: "gears", name: "Gaming Gear" }
            ],
            products: [
                {
                    id: "PROD_WEBSPIRE",
                    name: "WebSpire: The Tower",
                    category: "roguelike",
                    categoryName: "Thẻ Bài & Roguelike",
                    price: 199000,
                    originalPrice: 250000,
                    image: "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600&auto=format&fit=crop&q=80",
                    badge: "HOT GAME",
                    rating: 4.9,
                    reviewCount: 1240,
                    description: "Siêu phẩm game thẻ bài chiến thuật roguelike leo tháp đỉnh cao. Tích hợp trực tiếp Cloud Save và Bảng xếp hạng liên kết GameHub!",
                    inStock: true,
                    specs: {
                        os: "Windows 10/11, WebGL Browser",
                        ram: "4GB RAM",
                        storage: "500MB trống"
                    }
                },
                {
                    id: "PROD_CYBER_BLADE",
                    name: "CyberBlade: Neon City",
                    category: "games",
                    categoryName: "Game Bản Quyền",
                    price: 380000,
                    originalPrice: 450000,
                    image: "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=600&auto=format&fit=crop&q=80",
                    badge: "SALE",
                    rating: 4.8,
                    reviewCount: 890,
                    description: "Hành động chặt chém thế giới mở Cyberpunk tương lai với đồ họa Ray Tracing siêu thực.",
                    inStock: true,
                    specs: {
                        os: "Windows 10 64-bit",
                        ram: "16GB RAM",
                        storage: "65GB SSD"
                    }
                },
                {
                    id: "PROD_SHADOW_SOULS",
                    name: "Shadow of Eldoria",
                    category: "games",
                    categoryName: "Game Bản Quyền",
                    price: 590000,
                    originalPrice: 690000,
                    image: "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=600&auto=format&fit=crop&q=80",
                    badge: "BESTSELLER",
                    rating: 5.0,
                    reviewCount: 3120,
                    description: "Hành trình Souls-like đầy thử thách qua lục địa bóng đêm huyền bí.",
                    inStock: true,
                    specs: {
                        os: "Windows 11",
                        ram: "16GB RAM",
                        storage: "80GB SSD"
                    }
                },
                {
                    id: "PROD_GEAR_MOUSE",
                    name: "Chuột Gaming ApexSpeed Wireless RGB",
                    category: "gears",
                    categoryName: "Gaming Gear",
                    price: 850000,
                    originalPrice: 1100000,
                    image: "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600&auto=format&fit=crop&q=80",
                    badge: "GEAR",
                    rating: 4.7,
                    reviewCount: 420,
                    description: "Cảm biến 26.000 DPI, trọng lượng siêu nhẹ 58g, kết nối không dây 2.4GHz siêu tốc độ không độ trễ.",
                    inStock: true,
                    specs: {
                        dpi: "26,000 DPI",
                        weight: "58 gram",
                        battery: "Lên đến 90 giờ"
                    }
                },
                {
                    id: "PROD_GEAR_KEYBOARD",
                    name: "Bàn Phím Cơ MechMaster Pro TKL",
                    category: "gears",
                    categoryName: "Gaming Gear",
                    price: 1450000,
                    originalPrice: 1800000,
                    image: "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&auto=format&fit=crop&q=80",
                    badge: "NEW",
                    rating: 4.9,
                    reviewCount: 230,
                    description: "Switch quang học hotswap, foam tiêu âm cao cấp, led RGB từng phím tùy biến không giới hạn.",
                    inStock: true,
                    specs: {
                        layout: "Tenkeyless 87 phím",
                        switch: "Optical Linear Red",
                        keycaps: "PBT Double-shot"
                    }
                },
                {
                    id: "PROD_GEAR_HEADSET",
                    name: "Tai Nghe Gaming HyperSurround 7.1",
                    category: "gears",
                    categoryName: "Gaming Gear",
                    price: 990000,
                    originalPrice: 1290000,
                    image: "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=600&auto=format&fit=crop&q=80",
                    badge: "POPULAR",
                    rating: 4.8,
                    reviewCount: 650,
                    description: "Âm thanh vòm giả lập 7.1 định vị tiếng bước chân chuẩn xác, đệm tai mút hoạt tính êm ái.",
                    inStock: true,
                    specs: {
                        driver: "50mm Neodymium",
                        mic: "Chống ồn AI ENC",
                        connection: "USB / 3.5mm"
                    }
                }
            ],
            orders: []
        };
        fs.writeFileSync(DB_FILE, JSON.stringify(defaultData, null, 2), 'utf-8');
    }
}

function readData() {
    initDB();
    try {
        const raw = fs.readFileSync(DB_FILE, 'utf-8');
        const parsed = JSON.parse(raw);
        if (!parsed.products || parsed.products.length === 0) {
            // Đồng bộ dữ liệu mặc định nếu chưa có
            const tmpDefault = JSON.parse(fs.readFileSync(DB_FILE, 'utf-8'));
            parsed.categories = parsed.categories || [
                { id: "all", name: "Tất cả" },
                { id: "games", name: "Game Bản Quyền" },
                { id: "roguelike", name: "Thẻ Bài & Roguelike" },
                { id: "gears", name: "Gaming Gear" }
            ];
            parsed.products = parsed.products || [];
            parsed.orders = parsed.orders || [];
        }
        return parsed;
    } catch (e) {
        console.error("Lỗi đọc file database.json, trả về dữ liệu rỗng:", e);
        return { users: [], profiles: {}, leaderboards: [], coupons: [], products: [], categories: [], orders: [] };
    }
}

function writeData(data) {
    try {
        const tmpFile = DB_FILE + '.tmp';
        fs.writeFileSync(tmpFile, JSON.stringify(data, null, 2), 'utf-8');
        fs.renameSync(tmpFile, DB_FILE);
    } catch (e) {
        console.error("Lỗi ghi file database.json an toàn:", e);
        try {
            fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2), 'utf-8');
        } catch (err) {
            console.error("Lỗi ghi đè trực tiếp:", err);
        }
    }
}

function hashPassword(password) {
    return crypto.createHash('sha256').update(password).digest('hex');
}

function generateToken(userId) {
    const payload = `${userId}:${Date.now()}:${crypto.randomBytes(8).toString('hex')}`;
    return Buffer.from(payload).toString('base64');
}

function verifyToken(token) {
    if (!token) return null;
    try {
        if (token === 'offline-token-guest') {
            return { userId: 'GUEST', isGuest: true };
        }
        const decoded = Buffer.from(token, 'base64').toString('utf-8');
        const parts = decoded.split(':');
        if (parts.length >= 3) {
            const userId = parts[0];
            return { 
                userId, 
                timestamp: parseInt(parts[1]),
                isGuest: userId.startsWith('GUEST_') || userId === 'guest'
            };
        }
    } catch (e) {
        return null;
    }
    return null;
}

module.exports = {
    initDB,
    readData,
    writeData,
    hashPassword,
    generateToken,
    verifyToken
};
