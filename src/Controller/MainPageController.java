package Controller;

import Dictionary.Dictionary;
import Reader.TranslateResult;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainPageController {

    private boolean flag = true;
    private String srcWord;
    private String result;
    private String EnglishAccentUrl;
    private String AmericanAccentUrl;
    private String EnglishPhoneticSymbol;
    private String AmericanPhoneticSymbol;
    @FXML
    TextArea textArea;

    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File filePath = fileChooser.showOpenDialog(new Stage());
        if (filePath != null){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            textArea.setWrapText(true);
            textArea.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.getClickCount() == 2){

                        srcWord = textArea.getSelectedText().trim();
                        System.out.println(srcWord);
                        //翻译
                        result = search(srcWord);
                        System.out.println(result);
                        EnglishAccentUrl = Dictionary.EnglishAccentUrl;
                        AmericanAccentUrl = Dictionary.AmericanAccentUrl;
                        EnglishPhoneticSymbol = Dictionary.EnglishPhoneticSymbol;
                        AmericanPhoneticSymbol = Dictionary.AmericanPhoneticSymbol;
                        System.out.println(EnglishPhoneticSymbol + "\n" + AmericanPhoneticSymbol);
                        try {
                            new TranslateResult().showWindow(srcWord,EnglishPhoneticSymbol,EnglishAccentUrl,AmericanPhoneticSymbol,AmericanAccentUrl,
                                    result,event.getScreenX(),event.getScreenY());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }});

            textArea.setEditable(false);

            //一行一行读取至BufferReader并输出到textArea
            String str_line;
            while ((str_line = bufferedReader.readLine()) != null) {
                if (flag) {
                    textArea.setText(str_line);
                    flag = false;
                } else {
                    textArea.setText(textArea.getText() + "\n" + str_line);
                }
            }
        }
    }

    /**
     * 访问网络获取翻译结果
     * @param srcWord 待翻译的单词
     * @return 返回翻译结果
     */
    private String search(String srcWord) {
        //调用httpRequest方法，获取html字符串
        String html = Dictionary.httpRequest("http://www.iciba.com/" + srcWord);
        //利用正则表达式，抓取单词翻译信息
        String result = Dictionary.GetResult(html);

        return result;
    }



}
