package work.vietdefi;

import com.google.gson.JsonObject;
import work.vietdefi.clean.services.SharedServices;
import work.vietdefi.clean.services.user.UserService;

public class Main {
    public static void main(String[] args) {
        try {
            // Khởi tạo các dịch vụ dùng chung (bao gồm cả kết nối đến database)
            SharedServices.init();

            // Tạo instance của UserService sử dụng SqlJavaBridge đã được khởi tạo
            UserService userService = new UserService(SharedServices.sqlJavaBridge, SharedServices.USER_TABLE);

            // Thử đăng ký người dùng
            JsonObject registerResponse = userService.register("testuser", "testpassword");
            System.out.println("Register response: " + registerResponse.toString());

            // Thử đăng nhập người dùng
            JsonObject loginResponse = userService.login("testuser", "testpassword");
            System.out.println("Login response: " + loginResponse.toString());

            // Lấy token từ kết quả đăng nhập và thực hiện xác thực (authorization)
            String token = loginResponse.getAsJsonObject("d").get("token").getAsString();
            JsonObject authResponse = userService.authorization(token);
            System.out.println("Authorization response: " + authResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
