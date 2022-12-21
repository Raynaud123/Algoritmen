import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Inlezer {


    public static JSONObject inlezenJSON(String path){
        JSONParser jsonParser = new JSONParser();



        try(FileReader reader = new FileReader(path))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll((Map) obj);

            return jsonObject;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
