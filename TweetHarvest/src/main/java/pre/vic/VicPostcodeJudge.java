package pre.vic;

import org.json.JSONArray;

import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.List;

public class VicPostcodeJudge {
    private JSONArray cords = new JSONArray();
    private HashMap<Path2D, String> postcodeAreas = new HashMap<Path2D, String>();//

    public VicPostcodeJudge(List<JSONArray> cords, List<String> postid) {
        int numOfPostID = cords.size();//num of postID

        for (int i = 0; i < numOfPostID; i++) {
            JSONArray tmp = new JSONArray();//record the most inner array
            if (tmp.isNull(0)) {
                tmp = cords.get(i);
            }
            int layerNum = 0;
            while (tmp.length() == 1) {
                tmp = tmp.getJSONArray(0);
                layerNum++;
            }
            if (layerNum < 2) {//temporarily judge only once
                for (int j = 0; j < tmp.length(); j++) {
                    JSONArray tmpInner = tmp.getJSONArray(j);//inner array
                    while (tmpInner.length() == 1) {
                        tmpInner = tmpInner.getJSONArray(0);
                    }
                    drawPolygon(tmpInner, postid.get(i));
                }
            } else if (layerNum == 2) {
                drawPolygon(tmp, postid.get(i));
            } else {
                System.out.println("error");
            }
        }
    }

    private void drawPolygon(JSONArray cordSet, String postid) {
        //now begin to read cords in each postID
        Path2D path = new Path2D.Double();
        int k = cordSet.length();
        double longitude[] = new double[k];
        double latitude[] = new double[k];
        for (int num = 0; num < k; num++) {
            JSONArray cord = (JSONArray) cordSet.get(num);
            longitude[num] = cord.getDouble(0);
            latitude[num] = cord.getDouble(1);
            if (num == 0) {
                path.moveTo(longitude[0], latitude[0]);
            } else {
                path.lineTo(longitude[num], latitude[num]);
            }
        }
        postcodeAreas.put(path, postid);
        path.closePath();
    }

    public String judge(double longitude, double latitude) {
        for (Path2D postcodeArea : postcodeAreas.keySet()) {
            if (postcodeArea.contains(longitude, latitude)) {
                return postcodeAreas.get(postcodeArea);
            }
        }
        return null;
    }
}
