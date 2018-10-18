import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main extends Application {
    public static void main(String[] args) {launch(args);}

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setWidth(500);
        primaryStage.setHeight(500);
        primaryStage.setResizable(true);
        primaryStage.setTitle("Allo");
        BorderPane borderPane = new BorderPane();

        //Menu
        MenuItem lignes = new MenuItem("Lignes");
        MenuItem regions = new MenuItem("Regions");
        MenuItem barres = new MenuItem("Barres");
        MenuItem png = new MenuItem("PNG");
        MenuItem gif = new MenuItem("GIF");

        Menu importer = new Menu("Importer");
        importer.getItems().addAll(lignes, regions, barres);

        Menu exporter = new Menu("Exporter");
        exporter.getItems().addAll(png, gif);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(importer, exporter);

        //Actions menu
        LineChart<String,Number> lineChart = new LineChart<String,Number>(genX(),genY());
        lignes.setOnAction((event) -> {
            lineChart.setTitle("Températures moyennes");
            lineChart.getData().addAll(genererLignes(select(primaryStage)));
            if (lineChart.getData().size()!=0)
                lineChart.getData().remove(0);

            borderPane.setCenter(lineChart);
        });

        AreaChart<String,Number> areaChart = new AreaChart<>(genX(),genY());
        regions.setOnAction((event) -> {
            areaChart.setTitle("Températures moyennes");
            areaChart.getData().addAll(genererLignes(select(primaryStage)));
            if (areaChart.getData().size()!=0)
                areaChart.getData().remove(0);

            borderPane.setCenter(areaChart);
        });

        BarChart<String,Number> barChart = new BarChart<>(genX(),genY());
        barres.setOnAction((event) -> {
            barChart.setTitle("Températures moyennes");
            barChart.getData().addAll(genererLignes(select(primaryStage)));
            if (barChart.getData().size()!=1)
                barChart.getData().remove(0);

            borderPane.setCenter(barChart);
        });

        png.setOnAction((event) -> {
            if (borderPane.getCenter()==lineChart)
                saveAsPng(lineChart, primaryStage);

            if (borderPane.getCenter()==areaChart)
                saveAsPng(areaChart, primaryStage);

            if (borderPane.getCenter()==barChart)
                saveAsPng(barChart, primaryStage);
        });

        gif.setOnAction((event) -> {
            if (borderPane.getCenter()==lineChart)
                saveAsGif(lineChart, primaryStage);

            if (borderPane.getCenter()==areaChart)
                saveAsGif(areaChart, primaryStage);

            if (borderPane.getCenter()==barChart)
                saveAsGif(barChart, primaryStage);
        });



        //Disposition
        borderPane.setTop(menuBar);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public XYChart.Series genererLignes(File file){
        try {
            List<String> allLines = Files.readAllLines(Paths.get(file.getPath()));

            XYChart.Series series = new XYChart.Series();
            series.setName("Données");

            String chaine = allLines.get(0);
            String[] mois = chaine.split(",");

            chaine = allLines.get(1);
            String[] temp = chaine.split(",");

            int[] temperature = new int[temp.length];
            for (int i = 0; i < temp.length; i++){
                temperature[i] = Integer.parseInt(temp[i].trim());
            }

            for (int i=0; i<temperature.length; i++){
                series.getData().add(new XYChart.Data(mois[i], temperature[i]));
            }

            return series;
        }catch (Exception e){
            System.out.println("ok");
            XYChart.Series series = new XYChart.Series();
            series.setName("Données");
            series.getData().add(new XYChart.Data("i", 0));
            return series;
        }
    }

    public CategoryAxis genX(){
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mois");
        return xAxis;
    }

    public NumberAxis genY(){
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Température");
        return yAxis;
    }

    public File select(Stage primaryStage){
        try {
            FileChooser fc = new FileChooser();
            fc.setTitle("Veuillez sélectionner un fichier");
            File fichier = fc.showOpenDialog(primaryStage);
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichier dat", "*.dat"
                    ));
            return fichier;
        }catch (Exception e){
            return null;
        }
    }

    public void saveAsPng(Chart chart, Stage primaryStage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Veuillez sélectionner un fichier");

        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier png", "*.png"
                ));

        File fichier = fc.showSaveDialog(primaryStage);



        WritableImage image = chart.snapshot(new SnapshotParameters(), null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", fichier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAsGif(Chart chart, Stage primaryStage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Veuillez sélectionner un fichier");

        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichier gif", "*.gif"
                ));

        File fichier = fc.showSaveDialog(primaryStage);

        WritableImage image = chart.snapshot(new SnapshotParameters(), null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "gif", fichier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
