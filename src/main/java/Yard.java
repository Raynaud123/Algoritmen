import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Yard {

    Coördinaat[][][] matrix;
    int hoogte;
    HashMap<Integer,Integer> mapping_id_xcoor;


    public void createYard(JSONArray slots, int lengte, int breedte, int hoogte){


        mapping_id_xcoor = new HashMap<>();
        this.hoogte = hoogte;


        matrix = new Coördinaat[lengte][breedte][hoogte];
        for (int i = 0; i < slots.size(); i++){
            JSONObject slot = new JSONObject();
            slot.putAll((Map) slots.get(i));
            int x = ((Long) slot.get("x")).intValue();
            int y = ((Long) slot.get("y")).intValue();
            mapping_id_xcoor.put(((Long) slot.get("id")).intValue(),x);
            for (int j = 0;  j < hoogte; j++){
                matrix[x][y][j] = new Coördinaat(x,y,j,((Long) slot.get("id")).intValue());
            }
        }

    }

    public void addContainer(Object slot_id, Container container) {
        JSONArray slots = (JSONArray) slot_id;
        if(container.length == slots.size()){
            for(int i = 0; i < container.length; i++){
                int breakVar = Integer.MIN_VALUE;
                int x  = mapping_id_xcoor.get(((Long) slots.get(i)).intValue());
                for(int j = 0; 0 < matrix[x].length && breakVar != Integer.MAX_VALUE; j ++){
                    if (matrix[x][j][0].getId() == ((Long) slots.get(i)).intValue()){
                        for(int h = 0; h < hoogte; h++){
                            if(matrix[x][j][h].getContainer_id() == Integer.MIN_VALUE){
                                matrix[x][j][h].setContainer_id(container.id);
                                breakVar = Integer.MAX_VALUE;
                                break;
                            }
                    }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Level 0 \n");
        for(int i = 0; i < matrix.length; i ++){
            for (int j = 0; j < matrix[i].length;j++){
                str.append(matrix[i][j][0]);
            }
        }
        str.append("\n");

        str.append("Level 1 \n");
        for(int i = 0; i < matrix.length; i ++){
            for (int j = 0; j < matrix[i].length;j++){
                str.append(matrix[i][j][1]);
            }
        }
        str.append("\n");


        return str.toString();
    }
}
