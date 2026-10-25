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
            ]
        };
        fs.writeFileSync(DB_FILE, JSON.stringify(defaultData, null, 2), 'utf-8');
    }
}

function readData() {
    initDB();
    try {
        const raw = fs.readFileSync(DB_FILE, 'utf-8');
        return JSON.parse(raw);
    } catch (e) {
        console.error("Lỗi đọc file database.json, trả về dữ liệu rỗng:", e);
        return { users: [], profiles: {}, leaderboards: [], coupons: [] };
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
