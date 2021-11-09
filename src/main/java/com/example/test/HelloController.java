package com.example.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelloController {
    @FXML
    public TextField _search;
    @FXML
    public TextField _category;
    @FXML
    public TextField _number_comments_from;
    @FXML
    public TextField _number_comments_to;
    @FXML
    public TextField _number_likes_from;
    @FXML
    public TextField _number_likes_to;
    @FXML
    public TextField _per_page;
    @FXML
    public Label labelPage;
    @FXML
    public TextField _wight_imj;
    @FXML
    public Button send;
    @FXML
    public Button pref;
    @FXML
    public Button next;
    @FXML
    public ImageView imageView_01;
    @FXML
    public HBox hBox;

    public int page;
    public int countPages;
    public String search;
    public String category;
    public int number_comments_from;
    public int number_comments_to;
    public int number_likes_from;
    public int number_likes_to;
    public int per_page;
    public int wight_imj;



    private List<Map<String,String>> hits;

    @FXML
    public void onSendButtonClick(ActionEvent actionEvent) {
        page = 0;
        labelPage.setText("page: "+page);
        hits = getHits();
        getGalleryView(actionEvent);
    }

    @FXML
    public void onPrefButtonClick(ActionEvent actionEvent) {
        if (page > 1) {
            page--;
            labelPage.setText("page: "+String.valueOf(page));
        }
        getGalleryView(actionEvent);
    }

    @FXML
    public void onNextButtonClick(ActionEvent actionEvent) {
        if (page < countPages) {
            page++;
            labelPage.setText("page: "+String.valueOf(page));
            getGalleryView(actionEvent);
        }
    }

    @FXML
    public void initialize(){
        search = "boobs";
        category = "people";
        number_comments_from = 0;
        number_comments_to = 0;
        number_likes_from = 0;
        number_likes_to = 0;
        per_page = 20;
        wight_imj = 100;
    }

    public List<Map<String,String>> getHits(){
        update();
        String query = "https://pixabay.com/api/?" +
                "key=23798924-17f42c690434b5dec74a9c318" +
                "&image_type=photo" +
                "&q="+search +
                "&category=" + category;
        Response response = null;
        List<Map<String, String>> receivedHits = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            int totalHits = objectMapper.readValue(getJson(query), Response.class).getTotalHits();
            final int PER_PAGE = 200;
            for (int page = 1, j = totalHits; j > 0; page++, j-=PER_PAGE) {
                receivedHits.addAll(objectMapper.readValue(getJson(query+"&page="+page+"&per_page="+(Math.min(totalHits, PER_PAGE))), Response.class).getHits());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

        List<Map<String, String>> filteredHits = new ArrayList<>();
        for (Map<String,String> hit: receivedHits) {
            if (Integer.parseInt(hit.get("comments")) >= number_comments_from &&
                    (Integer.parseInt(hit.get("comments")) <= number_comments_to || number_comments_to == 0) &&
                    Integer.parseInt(hit.get("likes")) >= number_likes_from &&
                    (Integer.parseInt(hit.get("likes")) <= number_likes_to || number_likes_to == 0)){
                filteredHits.add(hit);
            }
        }

        countPages = filteredHits.size() / per_page;
        return filteredHits;
    }

    private static String getJson(String query){
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(query).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.connect();

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "cp1251"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null){
                    sb.append(line);
                }
                return sb.toString();
            } else {
                System.out.println("Fail response " + connection.getResponseCode() + ", " + connection.getResponseMessage());
            }

        } catch (Throwable cause){
            System.out.println(cause.getMessage());
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    public void getGalleryView(ActionEvent actionEvent)
    {
        update();
        ImageView imageView;
        hBox.getChildren().clear();
        int start = page * per_page + 1;
        int end = start + per_page;
        if (end > hits.size()){
            end = hits.size();
        }
        System.out.println("hits size="+hits.size());
        System.out.println("start view hits="+start);
        System.out.println("end view hits="+end);
        List<Map<String, String>> viewHits= hits.subList(start, end);
        for (Map<String, String> hit : viewHits) {
            imageView = new ImageView(new Image(hit.get("previewURL"), true));
            imageView.setFitWidth(wight_imj);
            hBox.getChildren().add(imageView);
        }
        System.out.println("count pages="+countPages);
    }

    public void update(){

        search = "boobs";
        category = "people";
        if (!_number_comments_from.getText().equals(""))
            number_comments_from = Integer.parseInt(_number_comments_from.getText());
        if (!_number_comments_to.getText().equals(""))
            number_comments_to = Integer.parseInt(_number_comments_to.getText());
        if (!_number_likes_from.getText().equals(""))
            number_likes_from = Integer.parseInt(_number_likes_from.getText());
        if (!_number_likes_to.getText().equals(""))
            number_likes_to = Integer.parseInt(_number_likes_to.getText());
        if (!_per_page.getText().equals(""))
            per_page = Integer.parseInt(_per_page.getText());
        if (!_wight_imj.getText().equals(""))
            wight_imj = Integer.parseInt(_wight_imj.getText());
    }
}
