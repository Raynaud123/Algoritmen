import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import javafx.scene.shape.Rectangle;
import org.json.simple.JSONObject;
import org.w3c.dom.css.Rect;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Main extends Application {

    //methodes is er conflict, zo ja conflict pas het pad aan om conflict te vermijden
    public static void main(String[] args){
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        Pane background = new Pane();

        new Inlezer();

        //Path ingeven
        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/2mh/MH2Terminal_20_10_3_2_100.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/4mh/MH2Terminal_20_10_3_2_160.json");
        //JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/1t/TerminalA_20_10_3_2_100.json");
         //JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/3t/TerminalA_20_10_3_2_160.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/5t/TerminalB_20_10_3_2_160.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/6t/Terminal_10_10_3_1_100.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/7t/TerminalC_10_10_3_2_80.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/8t/TerminalC_10_10_3_2_80.json");
        //       JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/9t/TerminalC_10_10_3_2_100.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/10t/TerminalC_10_10_3_2_100.json");
//        JSONObject data = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/Voorbeeld1/terminal22_1_100_1_10.json");
        int maxHeight = 0;
        int width = 0;
        int length = 0;
        int targetHeight = Integer.MIN_VALUE;
        JSONArray slots = null;
        JSONArray assignments = null;
        JSONArray cranes = null;
        JSONArray containers = null;
        JSONArray targetassignments = null;


        assert data != null;
        if(data.containsKey("maxheight")){
            maxHeight = (int) ((long) data.get("maxheight"));
        }
        if(data.containsKey("width")){
            width = (int) ((long)data.get("width"));
        }
        if(data.containsKey("length")){
            length = (int) ((long)data.get("length"));
        }
        if(data.containsKey("targetheight")){
            targetHeight= (int) ((long) data.get("targetheight"));
        }
        else{
            //        JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/Voorbeeld1/terminal22_1_100_1_10target.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/1t/targetTerminalA_20_10_3_2_100.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/3t/targetTerminalA_20_10_3_2_160.json");
            JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/5t/targetTerminalB_20_10_3_2_160UPDATE.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/6t/targetTerminal_10_10_3_1_100.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/7t/targetTerminalC_10_10_3_2_80.json");
            //JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/8t/targetTerminalC_10_10_3_2_80.json");
            //           JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/9t/targetTerminalC_10_10_3_2_100.json");
//         JSONObject target = Inlezer.inlezenJSON(System.getProperty("user.dir") + "/src/Inputs/10t/targetTerminalC_10_10_3_2_100.json");
            targetassignments = (JSONArray) target.get("assignments");
        }
        if(data.containsKey("slots")){
            slots = (JSONArray) data.get("slots");
        }
        if (data.containsKey("assignments")){
            assignments = (JSONArray) data.get("assignments");
        }
        if(data.containsKey("containers")){
            containers = (JSONArray) data.get("containers");
        }
        if(data.containsKey("cranes")){
            cranes =(JSONArray) data.get("cranes");
        }



        Yard yard = new Yard();
        ArrayList<Container> containersArray =  new ArrayList<>();

        assert slots != null;
        yard.createYard(slots, length, width, maxHeight);

        // containers
        assert containers != null;
        for (Object value : containers) {
            JSONObject container = new JSONObject();
            container.putAll((Map) value);
            Container nieuw = new Container(container.get("id"), container.get("length"));
            containersArray.add(nieuw);
        }

        // assignments
        assert assignments != null;
        for (Object o : assignments) {
            JSONObject assignment = new JSONObject();
            assignment.putAll((Map) o);
            int index = Integer.MIN_VALUE;
            for(int i = 0; i < containersArray.size();i++){
                if(((Long) assignment.get("container_id")).intValue() == containersArray.get(i).getId()){
                    index = i;
                    break;
                }
            }
            yard.addContainer(assignment.get("slot_id"), containersArray.get(index));
        }

        assert cranes != null;
        yard.addCranes(cranes);
        //System.out.println(yard.toString());


        Random rand = new Random();




        ArrayList<Container> mapped = new ArrayList<>();
        for(int i = 0;i < yard.getLengte(); i++){
            for (int j = 0;j <  yard.getBreedte(); j++){
                for(int h = 0; h < yard.hoogte; h++){
                    if(yard.getMatrix()[i][j][h].getContainer_id() != Integer.MIN_VALUE){
                        Container c = null;
                        for (Container cont: containersArray){
                            if(cont.getId() == yard.getMatrix()[i][j][h].getContainer_id()){
                                c=cont;
                                break;
                            }
                        }
                        if(!mapped.contains(c)){
                            int r = rand.nextInt(255);
                            int g = rand.nextInt(255);
                            int b = rand.nextInt(255);
                            double op = rand.nextDouble(0.2,0.8);
                            Color randomColor = Color.rgb(r, g, b, 1);
                            StackPane stack = createRectangle(yard.lengte,yard.breedte,i,j,h,randomColor,c,Integer.MAX_VALUE-h);
                            background.getChildren().add(stack);
                            mapped.add(c);
                        }


                    }
                }
            }
        }

        for (int i =0; i < yard.getLengte();i++){
            for (int j =0; j < yard.getBreedte();j++){
                Text text = new Text("slot-id:" + yard.getMatrix()[i][j][0].getId());
                int lengteContainer = 1200 / yard.getLengte();
                int breedteContainer = 700 / yard.getBreedte();
                HBox test = new HBox(text);
                test.setLayoutX(lengteContainer*i);
                test.setLayoutY(breedteContainer*j);
                background.getChildren().add(test);
            }
        }
        for (int i=0; i<maxHeight; i++) {
            new Gui("Hoogte " + i, yard, i);
        }



        HBox pane = new HBox(background);
        VBox test = new VBox(pane);
        pane.setAlignment(Pos.CENTER);
        test.setAlignment(Pos.CENTER);

        for (Kraan c: yard.getCranes()){
            float breedte = 700/yard.getBreedte();
            float lengte = 1200/ yard.getLengte();
            Rectangle kraan = new Rectangle();
            kraan.setHeight(750);
            double startY = kraan.getY();
            kraan.setY(startY-25);
            kraan.setWidth(breedte/4);
            kraan.setViewOrder(0);
            kraan.setTranslateX(c.getX()*lengte);
            kraan.setFill(Color.BLACK);
            kraan.setId("k" + c.getId());

            Rectangle grijper = new Rectangle();
            grijper.setId("g" + c.getId());
            System.out.println("id: " + grijper.getId());
            grijper.setHeight(lengte/2);
            grijper.setWidth(breedte/2);
            grijper.setViewOrder(0);
            grijper.setTranslateY((c.getY()*breedte)+ (breedte/4));
            grijper.setTranslateX((c.getX()*lengte) - (lengte/7) );
            grijper.setFill(Color.BLACK);

            background.getChildren().add(kraan);
            background.getChildren().add(grijper);

        }

        Scene scene = new Scene(test,1400,800);
        stage.setScene(scene);
        stage.show();

        if(targetHeight==Integer.MIN_VALUE){
            yard.calculateMovementsTargetAssignments(targetassignments,containersArray);
        }else {
            yard.calculateMovementsTargetHeight(maxHeight, targetHeight,containersArray);
        }

        ArrayList<Beweging> solutions = yard.solution;
        ParallelTransition sequence = new ParallelTransition();
        Button pause = new Button("Pause");
        Button play = new Button("Play");
        VBox buttons = new VBox(play,pause);
        pane.getChildren().add(buttons);


        for(Kraan c: yard.cranes){
            float breedte = 700/yard.getBreedte();
            float lengte = 1200/ yard.getLengte();
            Timeline kraanTimeLine = new Timeline();
            Timeline grijperTimeLine = new Timeline();
            kraanTimeLine.setCycleCount(1);
            kraanTimeLine.setAutoReverse(true);
            grijperTimeLine.setCycleCount(1);
            grijperTimeLine.setAutoReverse(true);

            Rectangle grijper  = (Rectangle) background.lookup("#g"+c.getId());
            Rectangle kraan = (Rectangle) background.lookup("#k"+c.getId());

            for (Beweging b : solutions){
                if(b.getKraan_id() == c.getId()){
                    if(b.isTussenBeweging()){
                        int eindX = b.getEind().getX();
                        float eindY = b.getEind().getY();
                        final KeyValue ks = new KeyValue(kraan.translateXProperty(), eindX*lengte);
                        final KeyFrame kfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()), ks);
                        final KeyValue gsx = new KeyValue(grijper.translateXProperty(), eindX*lengte);
                        final KeyValue gsy = new KeyValue(grijper.translateYProperty(), eindY*breedte+0.5);
                        final KeyFrame gfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()), gsx,gsy);
                        kraanTimeLine.getKeyFrames().add(kfs);
                        grijperTimeLine.getKeyFrames().add(gfs);
                    }else {
                        int eindX = b.getEind().getX();
                        float eindY = b.getEind().getY();
                        final KeyValue ks = new KeyValue(kraan.translateXProperty(), eindX*lengte);
                        final KeyFrame kfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()), ks);
                        final KeyValue gsx = new KeyValue(grijper.translateXProperty(), eindX*lengte);
                        final KeyValue gsy = new KeyValue(grijper.translateYProperty(), eindY*breedte+0.5);
                        final KeyFrame gfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()), gsx,gsy);
//
                        kraanTimeLine.getKeyFrames().add(kfs);
                        grijperTimeLine.getKeyFrames().add(gfs);
                    }
                }

            }


            sequence.getChildren().add(kraanTimeLine);
            sequence.getChildren().add(grijperTimeLine);
        }




        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                sequence.pause();
            }
        });
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                sequence.play();
            }
        });





//        for (Kraan k : yard.cranes){
//            System.out.println(k.getId() + ": " + k.getBewegingLijst());
//            System.out.println();
//        }

        for(Beweging b: yard.solution){
            if(b.getKraan_id() == 0){
                System.out.println(b);
            }
        }

        System.out.println();

        for(Beweging b: yard.solution){
            if(b.getKraan_id() == 1){
                System.out.println(b);
            }
        }


    }

    private StackPane createRectangle(int yardLengte, int yardBreedte, int x, int y, int h, Color randomColor, Container c, int priority) {
        int container_length = c.getLength();
        int lengteContainer = 1200 / yardLengte;
        int breedteContainer = 700 / yardBreedte;
        Rectangle nieuw = new Rectangle(lengteContainer*c.getLength(),breedteContainer);
        nieuw.setFill(randomColor);
        final Text hoogte = new Text("hoogte: " + h);
        HBox bottom = new HBox(hoogte);
        final Text container_id = new Text("cont-id: " + c.getId());
        HBox center = new HBox(container_id);
        VBox hope = new VBox(center,bottom);
        hope.setAlignment(Pos.CENTER);
        final StackPane stack = new StackPane();
        stack.setViewOrder(priority);
        stack.setLayoutX(x*lengteContainer);
        stack.setLayoutY(y*breedteContainer);
        stack.getChildren().addAll(nieuw,hope);
        return stack;
    }
}
