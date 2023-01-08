import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Main extends Application {

    //methodes is er conflict, zo ja conflict pas de het pad aan om conflict te vermijden
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        new Inlezer();

        //path ingeven via CMD
        Parameters parameters = getParameters();

        //TargetHeight
        if(parameters.getRaw().size() == 2) {

            String inputFilePath = parameters.getRaw().get(0);
            String outputFilePath = parameters.getRaw().get(1);

            JSONObject data = Inlezer.inlezenJSON(inputFilePath);
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
            if (data.containsKey("maxheight")) {
                maxHeight = (int) ((long) data.get("maxheight"));
            }
            if (data.containsKey("width")) {
                width = (int) ((long) data.get("width"));
            }
            if (data.containsKey("length")) {
                length = (int) ((long) data.get("length"));
            }
            if (data.containsKey("targetheight")) {
                targetHeight = (int) ((long) data.get("targetheight"));
            }
            if (data.containsKey("slots")) {
                slots = (JSONArray) data.get("slots");
            }
            if (data.containsKey("assignments")) {
                assignments = (JSONArray) data.get("assignments");
            }
            if (data.containsKey("containers")) {
                containers = (JSONArray) data.get("containers");
            }
            if (data.containsKey("cranes")) {
                cranes = (JSONArray) data.get("cranes");
            }


            Yard yard = new Yard();
            ArrayList<Container> containersArray = new ArrayList<>();

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
                for (int i = 0; i < containersArray.size(); i++) {
                    if (((Long) assignment.get("container_id")).intValue() == containersArray.get(i).getId()) {
                        index = i;
                        break;
                    }
                }
                yard.addContainer(assignment.get("slot_id"), containersArray.get(index));
            }

            assert cranes != null;
            yard.addCranes(cranes);


            createGUI(yard,containersArray,stage,targetHeight, maxHeight, targetassignments);

            writeToFile(yard,outputFilePath);
        }//TargetAssignments
        else if (parameters.getRaw().size() == 3){
            String inputFilePath = parameters.getRaw().get(0);
            String targetFilePath = parameters.getRaw().get(1);
            String outputFilePath = parameters.getRaw().get(2);

            JSONObject data = Inlezer.inlezenJSON(inputFilePath);
            int maxHeight = 0;
            int width = 0;
            int length = 0;
            JSONArray slots = null;
            JSONArray assignments = null;
            JSONArray cranes = null;
            JSONArray containers = null;
            JSONArray targetassignments = null;


            assert data != null;
            if (data.containsKey("maxheight")) {
                maxHeight = (int) ((long) data.get("maxheight"));
            }
            if (data.containsKey("width")) {
                width = (int) ((long) data.get("width"));
            }
            if (data.containsKey("length")) {
                length = (int) ((long) data.get("length"));
            }
                JSONObject target = Inlezer.inlezenJSON(targetFilePath);
                targetassignments = (JSONArray) target.get("assignments");
            if (data.containsKey("slots")) {
                slots = (JSONArray) data.get("slots");
            }
            if (data.containsKey("assignments")) {
                assignments = (JSONArray) data.get("assignments");
            }
            if (data.containsKey("containers")) {
                containers = (JSONArray) data.get("containers");
            }
            if (data.containsKey("cranes")) {
                cranes = (JSONArray) data.get("cranes");
            }


            Yard yard = new Yard();
            ArrayList<Container> containersArray = new ArrayList<>();

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
                for (int i = 0; i < containersArray.size(); i++) {
                    if (((Long) assignment.get("container_id")).intValue() == containersArray.get(i).getId()) {
                        index = i;
                        break;
                    }
                }
                yard.addContainer(assignment.get("slot_id"), containersArray.get(index));
            }

            assert cranes != null;
            yard.addCranes(cranes);


            createGUI(yard,containersArray,stage,Integer.MIN_VALUE,maxHeight,targetassignments);

            writeToFile(yard,outputFilePath);
        }else{
            System.out.println("The CL arguments size doesn't equal 2 or 3");
            System.exit(1);
        }
    }

    private void writeToFile(Yard yard, String outputFilePath) throws IOException {
        //Write to file
        File file = new File(outputFilePath);
        if(file.createNewFile()){
            System.out.println(file.getName() + " is created");
        }else {
            System.out.println("Solution is saved in " + file.getName());
        }

        FileWriter writer = new FileWriter(file);
        writer.write("%CraneId;ContainerId;PickupTime;EndTime;PickupPosX;PickupPosY;EndPosX;EndPosY;\n");
        for (Beweging b : yard.solution){
            double startY = b.getStart().getY() + 0.5;
            double eindY = b.getEind().getY() + 0.5;
            writer.write(b.getKraan_id() + ";"+b.getId() +";"+b.getStartTijdstip()+";"+b.getEindTijdstip()+";"+b.getStart().getX()+";"+startY+";"+b.getEind().getX()+";"+ eindY);
            writer.write("\n");
        }
        writer.close();
    }

    private void createGUI(Yard yard, ArrayList<Container> containersArray, Stage stage, int targetHeight, int maxHeight, JSONArray targetassignments){

        Pane background = new Pane();

        Random rand = new Random();


        ArrayList<Container> mapped = new ArrayList<>();
        for (int i = 0; i < yard.getLengte(); i++) {
            for (int j = 0; j < yard.getBreedte(); j++) {
                for (int h = 0; h < yard.hoogte; h++) {
                    if (yard.getMatrix()[i][j][h].getContainer_id() != Integer.MIN_VALUE) {
                        Container c = null;
                        for (Container cont : containersArray) {
                            if (cont.getId() == yard.getMatrix()[i][j][h].getContainer_id()) {
                                c = cont;
                                break;
                            }
                        }
                        if (!mapped.contains(c)) {
                            int r = rand.nextInt(255);
                            int g = rand.nextInt(255);
                            int b = rand.nextInt(255);
                            double op = rand.nextDouble(0.2, 0.8);
                            Color randomColor = Color.rgb(r, g, b, 1);
                            StackPane stack = createRectangle(yard.lengte, yard.breedte, i, j, h, randomColor, c, Integer.MAX_VALUE - h);
                            background.getChildren().add(stack);
                            mapped.add(c);
                        }


                    }
                }
            }
        }

        for (int i = 0; i < yard.getLengte(); i++) {
            for (int j = 0; j < yard.getBreedte(); j++) {
                Text text = new Text("slot-id:" + yard.getMatrix()[i][j][0].getId());
                text.setViewOrder(2);
                int lengteContainer = 1200 / yard.getLengte();
                int breedteContainer = 700 / yard.getBreedte();
                HBox test = new HBox(text);
                test.setLayoutX(lengteContainer * i);
                test.setLayoutY(breedteContainer * j);
                test.setViewOrder(2);
                background.getChildren().add(test);
            }
        }

        HBox pane = new HBox(background);
        VBox test = new VBox(pane);
        pane.setAlignment(Pos.CENTER);
        test.setAlignment(Pos.CENTER);

        for (Kraan c : yard.getCranes()) {
            float breedte = 700 / yard.getBreedte();
            float lengte = 1200 / yard.getLengte();
            Rectangle kraan = new Rectangle();
            kraan.setHeight(750);
            double startY = kraan.getY();
            kraan.setY(startY - 25);
            kraan.setWidth(breedte / 4);
            kraan.setViewOrder(0);
            kraan.setTranslateX(c.getX() * lengte);
            kraan.setFill(Color.BLACK);
            kraan.setId("k" + c.getId());

            Rectangle grijper = new Rectangle();
            grijper.setId("g" + c.getId());
            grijper.setHeight(lengte / 2);
            grijper.setWidth(breedte / 2);
            grijper.setViewOrder(0);
            grijper.setTranslateY((c.getY() * breedte) + (breedte / 4));
            grijper.setTranslateX((c.getX() * lengte) - (lengte / 7));
            grijper.setFill(Color.BLACK);

            background.getChildren().add(kraan);
            background.getChildren().add(grijper);

        }

        Scene scene = new Scene(test, 1400, 800);
        stage.setScene(scene);
        stage.show();

        if (targetHeight == Integer.MIN_VALUE) {
            yard.calculateMovementsTargetAssignments(targetassignments, containersArray);
        } else {
            yard.calculateMovementsTargetHeight(maxHeight, targetHeight, containersArray);
        }


        ArrayList<Beweging> solutions = yard.solution;
        ParallelTransition sequence = new ParallelTransition();
        Button pause = new Button("Pause");
        Button play = new Button("Play");
        VBox buttons = new VBox(play, pause);
        pane.getChildren().add(buttons);


        for (Kraan c : yard.cranes) {
            float breedte = 700 / yard.getBreedte();
            float lengte = 1200 / yard.getLengte();
            Timeline kraanTimeLine = new Timeline();
            Timeline grijperTimeLine = new Timeline();
            kraanTimeLine.setCycleCount(1);
            kraanTimeLine.setAutoReverse(true);
            grijperTimeLine.setCycleCount(1);
            grijperTimeLine.setAutoReverse(true);

            Rectangle grijper = (Rectangle) background.lookup("#g" + c.getId());
            Rectangle kraan = (Rectangle) background.lookup("#k" + c.getId());

            for (Beweging b : solutions) {
                if (b.getKraan_id() == c.getId()) {
                    if (b.isTussenBeweging()) {
                        int eindX = b.getEind().getX();
                        float eindY = b.getEind().getY();
                        final KeyValue ks = new KeyValue(kraan.translateXProperty(), eindX * lengte);
                        final KeyFrame kfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()), ks);
                        final KeyValue gsx = new KeyValue(grijper.translateXProperty(), eindX * lengte);
                        final KeyValue gsy = new KeyValue(grijper.translateYProperty(), eindY * breedte + 0.5);
                        final KeyFrame gfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()), gsx, gsy);
                        kraanTimeLine.getKeyFrames().add(kfs);
                        grijperTimeLine.getKeyFrames().add(gfs);
                    } else {
                        Timeline containersTimeLine = new Timeline();
                        int eindX = b.getEind().getX();
                        float eindY = b.getEind().getY();
                        StackPane container = (StackPane) background.lookup("#c" + b.getId());

                        final KeyValue ks = new KeyValue(kraan.translateXProperty(), eindX * lengte);
                        final KeyFrame kfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()), ks);
                        final KeyValue gsx = new KeyValue(grijper.translateXProperty(), eindX * lengte);
                        final KeyValue gsy = new KeyValue(grijper.translateYProperty(), eindY * breedte + 0.5);
                        final KeyFrame gfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()), gsx, gsy);



                        containersTimeLine.setDelay(Duration.seconds(b.getStartTijdstip()-1));

                        final KeyValue csvB = new KeyValue(container.viewOrderProperty(), 1);
                        final KeyFrame cfsB = new KeyFrame(Duration.seconds(b.getEindTijdstip()-1-b.getStartTijdstip()),csvB);
                        containersTimeLine.getKeyFrames().add(cfsB);
                        VBox vBox =(VBox) container.getChildren().get(1);
                        HBox hBox = (HBox) vBox.getChildren().get(1);
                        Text hoogte = (Text) hBox.getChildren().get(0);
                        final KeyValue csxS = new KeyValue(container.translateXProperty(), eindX * lengte);
                        final KeyValue csyS = new KeyValue(container.translateYProperty(), eindY * breedte);
                        final KeyValue csvS = new KeyValue(container.viewOrderProperty(), Integer.MAX_VALUE - b.getEind().getZ());
                        final KeyValue tsvS = new KeyValue(hoogte.textProperty(),"hoogte: "  + b.getEind().getZ());
                        final KeyFrame cfs = new KeyFrame(Duration.seconds(b.getEindTijdstip()+1-b.getStartTijdstip()), csxS, csyS,csvS,tsvS);

                        kraanTimeLine.getKeyFrames().add(kfs);
                        grijperTimeLine.getKeyFrames().add(gfs);
                        containersTimeLine.getKeyFrames().add(cfs);
                        sequence.getChildren().add(containersTimeLine);
                    }
                }

            }


            sequence.getChildren().add(kraanTimeLine);
            sequence.getChildren().add(grijperTimeLine);
        }


        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sequence.pause();
            }
        });
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                sequence.play();
            }
        });


        stage.setOnHidden(e -> {
            Platform.exit();
        });

    }

    private StackPane createRectangle(int yardLengte, int yardBreedte, int x, int y, int h, Color randomColor, Container c, int priority) {
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
        stack.setTranslateX(x*lengteContainer);
        stack.setTranslateY(y*breedteContainer);
        stack.getChildren().addAll(nieuw,hope);
        stack.setId("c" + c.getId());
        return stack;
    }
}
