import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Yard {

    Coördinaat[][][] matrix;
    int hoogte;
    //Mapping between id en x-cöordinaat
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
                //If no container is assigned, container_id equals Integer_Min_Value
                matrix[x][y][j] = new Coördinaat(x,y,j,((Long) slot.get("id")).intValue());
            }
        }

    }

    public void addContainer(Object slot_id, Container container) {
        int slots = (int)((long) slot_id);
        int startX  = mapping_id_xcoor.get(slots);
        for(int j = 0; j < matrix[startX].length; j++) {
            if(matrix[startX][j][0].getId() == (slots)){
                boolean b = true;
                for (int h =0; h < hoogte && b; h++){
                    b = true;
                    if(matrix[startX][j][h].getContainer_id() == Integer.MIN_VALUE){
                        int count = 1;
                        for (int i = 1; i < container.length; i++){
                            if (matrix[startX+i][j][h].getContainer_id() == Integer.MIN_VALUE){
                                count++;
                            }
                        }
                        if (count == container.length){
                            b=false;
                            for (int i = 0; i < container.length; i++){
                                matrix[startX+i][j][h].setContainer_id(container.id);
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

    public void voerBewegingUit(int container_id, Coördinaat eind) {

    }
}
