package Controller;

import Constant.Constant;
import Login.Login;
import Reader.MainPage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

public class RegisterController {

    @FXML
    private TextField tfAccount;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfRePassword;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnSignUp;


    /**
     * 完成注册按钮
     * @param actionEvent
     */
    @FXML
    public void signUp(ActionEvent actionEvent) {

        if (tfAccount.getText().isEmpty() || pfPassword.getText().isEmpty() || pfRePassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "账号或密码不能为空！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else if (!(pfPassword.getText()).equals(pfRePassword.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "两次输入的密码不一致！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else {
            /*
             *  线程处理注册操作
             *  坑！！！
             *  用runable run方法会报Not on FX application thread; currentThread = thread1的异常。
             *  导致这个异常的原因就是因为修改界面的工作必须要由JavaFX的Application Thread来完成，由其它线程来完成不是线程安全的。
             *
             *  解决：Platform.runLater
             *  JavaFX提供的一个工具方法，可以把修改界面的工作放到一个队列中，等到Application Thread空闲的时候，它就会自动执行队列中修改界面的工作了
             */
            new Thread(()->{
                Platform.runLater(() -> {
                    try {
                        //调用注册模块
                        register();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }).start();
        }
    }

    /**
     * 返回按钮
     * @param event
     * @throws Exception
     */
    public void back(ActionEvent event) throws Exception {
        //启动注册窗口
        Login nextWindow = new Login();
        nextWindow.showWindow();
        //销毁当前窗口
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    /**
     * 注册模块
     * @throws IOException
     */
    private void register(){

        try {
            String account = tfAccount.getText();
            String password = pfPassword.getText();

            URL url = new URL(Constant.URL_Register + "account=" + account + "&" + "password=" + password);
            // 接收servlet返回值，是字节
            InputStream is = url.openStream();

            // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            //判断结果
            if (sb.toString().equals(Constant.FLAG_SUCCESS)) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("注册成功！");
                alert.setHeaderText(null);
                alert.setContentText("请点击\"确认\"进入主页");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){

                    //启动在线状态主页面
                    MainPage mainWindow = new MainPage();
                    mainWindow.showWindow();

                    //销毁当前窗口
                    Stage stage = (Stage) btnSignUp.getScene().getWindow();
                    stage.close();

                }
            } else if (sb.toString().equals(Constant.FLAG_YES)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "该账号已被注册！");
                alert.setHeaderText(null);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "注册失败！");
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        }catch (Exception e){
            //网络不通的情况
            Alert alert = new Alert(Alert.AlertType.ERROR, "网络连接异常！");
            alert.setHeaderText(null);
            alert.showAndWait();

            e.printStackTrace();
        }
    }
}