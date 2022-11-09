import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Inlezer {


    public static JSONObject inlezenJSON(){
        JSONParser jsonParser = new JSONParser();

//        URL url = Main.class.getResource("input.json");

        try (FileReader reader = new FileReader("C:\\Users\\User\\IdeaProjects\\Algoritmen\\src\\main\\input.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll((Map) obj);

            return jsonObject;
            //Iterate over employee array
            //input.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
