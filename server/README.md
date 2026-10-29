# WebSpire & GameHub Backend API Server

Máy chủ API đồng bộ trực tuyến cho game thẻ bài chiến thuật **WebSpire: The Tower** và đóng vai trò Backend API chính thức cho sàn thương mại điện tử **GameHub**.

## 🚀 Tính năng chính
1. **Xác thực (Authentication)**: Đăng ký & Đăng nhập người dùng / game thủ, cấp token phiên làm việc.
2. **Cửa hàng GameHub (E-Commerce Store)**:
   - Danh sách sản phẩm game và phụ kiện gaming gear (`/api/products`).
   - Danh mục thể loại (`/api/categories`).
   - Đặt hàng trực tuyến (`/api/orders`), hỗ trợ áp mã giảm giá thưởng từ game.
3. **Cloud Save & Sync (Tiến trình WebSpire)**:
   - Lưu tiến trình leo tháp (Tầng, Máu HP, Vàng, Bộ bài Master Deck, Cổ vật Relic).
   - Nạp lại lượt chơi đã lưu từ Cloud Server.
4. **Bảng Xếp Hạng Toàn Cầu (Global Leaderboard)**:
   - Ghi nhận kỷ lục leo tháp, điểm số, số quái tiêu diệt, trùm hạ gục và tầng cao nhất.
5. **Voucher GameHub Rewards (Liên kết Game - Shop)**:
   - Thưởng mã giảm giá GameHub dựa trên thành tích leo tháp trong game:
     - Tầng 5: Giảm 10% (`WEBSPIRE5`)
     - Tầng 8: Giảm 15% (`WEBSPIRE8`)
     - Tầng 15: Giảm 25% VIP (`WEBSPIRE15`)
6. **Cơ chế lưu trữ bền vững**: Tự động lưu trữ vào `data/database.json`.

## 🛠 Hướng dẫn Khởi chạy
Trong thư mục `server`:
```bash
npm install
npm start
```
Server mặc định lắng nghe tại: `http://localhost:5000`.

## 📡 Danh sách REST Endpoints

### 1. Hệ thống & Xác thực
- `GET  /api/health` - Kiểm tra trạng thái máy chủ
- `POST /api/auth/register` - Đăng ký tài khoản (`{ username, email, password }`)
- `POST /api/auth/login` - Đăng nhập (`{ username, password }`)

### 2. Cửa hàng GameHub (Store)
- `GET  /api/categories` - Danh sách danh mục sản phẩm
- `GET  /api/products?category=...&search=...` - Danh sách sản phẩm game và gear
- `GET  /api/products/:id` - Xem chi tiết một sản phẩm
- `POST /api/orders` - Tạo đơn hàng (hỗ trợ nhập coupon)
- `GET  /api/orders/user/:userId` - Xem lịch sử đơn hàng của người dùng

### 3. WebSpire Game & Voucher Rewards
- `GET  /api/game/profile/:userId` - Lấy thông tin bản lưu Cloud
- `POST /api/game/profile` - Lưu tiến trình ván chơi lên Cloud
- `GET  /api/game/leaderboard` - Xem bảng xếp hạng Top 20
- `POST /api/game/leaderboard` - Nộp kỷ lục điểm số
- `GET  /api/coupons` - Danh sách tất cả voucher khả dụng
- `GET  /api/coupons/validate/:code` - Kiểm tra tính hợp lệ của mã giảm giá
- `GET  /api/game/coupons/:floor` - Đổi mã giảm giá theo tầng leo tháp
- `GET  /api/user/unlocked-coupons/:userId` - Danh sách voucher đã mở khóa theo người dùng

