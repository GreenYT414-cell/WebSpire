# WebSpire Backend Server

Máy chủ đồng bộ trực tuyến cho game thẻ bài chiến thuật **WebSpire: The Tower** (kết nối hệ thống GameHub).

## 🚀 Tính năng chính
1. **Xác thực (Authentication)**: Đăng ký & Đăng nhập người chơi, cấp token phiên chơi.
2. **Cloud Save & Sync (Đồng bộ đám mây)**:
   - Lưu tiến trình leo tháp (Tầng hiện tại, Máu HP, Vàng, Bộ bài Master Deck, Cổ vật Relic).
   - Nạp lại lượt chơi đã lưu từ Cloud Server.
3. **Bảng Xếp Hạng Toàn Cầu (Global Leaderboard)**:
   - Ghi nhận điểm số, số quái tiêu diệt, trùm hạ gục, tầng cao nhất và thời gian hoàn thành.
4. **Voucher GameHub Rewards**:
   - Tự động tặng mã giảm giá GameHub dựa trên mốc tầng đạt được (Tầng 5: 10%, Tầng 8: 15%, Tầng 15: 25%).
5. **Cơ chế lưu trữ bền vững**: Tự động lưu trữ vào `data/database.json`, không phụ thuộc database ngoài phức tạp.

## 🛠 Hướng dẫn Khởi chạy
Trong thư mục `server`:
```bash
npm install
npm start
```
Server sẽ chạy tại: `http://localhost:8080`.

## 📡 Danh sách REST Endpoints
- `GET  /api/health` - Kiểm tra trạng thái máy chủ
- `POST /api/auth/register` - Đăng ký tài khoản mới (`{ username, email, password }`)
- `POST /api/auth/login` - Đăng nhập (`{ username, password }`)
- `GET  /api/game/profile/:userId` - Lấy thông tin bản lưu Cloud
- `POST /api/game/profile` - Lưu tiến trình ván chơi lên Cloud
- `GET  /api/game/leaderboard` - Xem bảng xếp hạng Top 20
- `POST /api/game/leaderboard` - Nộp kỷ lục điểm số
- `GET  /api/game/coupons/:floor` - Đổi mã giảm giá GameHub
