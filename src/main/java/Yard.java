import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;

public class Yard {


    Coördinaat[][][] matrix;

    public Yard() {
    }

    public void add() {

    }

    public void createYard(JSONArray slots, int lengte, int breedte, int hoogte){
        matrix = new Coördinaat[lengte][breedte][hoogte];
        for (int i = 0; i < slots.size(); i++){
            JSONObject slot = new JSONObject();
            slot.putAll((Map) slots.get(i));
            int x = (int) slot.get("x");
            int y = (int) slot.get("y");
            for (int j = 0;  j < hoogte; j++){
                matrix[x][y][j] = new Coördinaat(x,y,j,(int) slot.get("id"));
            }
        }

    }

}
