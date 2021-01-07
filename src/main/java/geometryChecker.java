import com.google.common.geometry.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class geometryChecker {

    private static Map<String, S2Polygon> s2PolygonMap = new HashMap<>();
    private static Map<String, S2CellUnion> s2CellUnionMap = new HashMap<>();
    private static Map<String, S2CellUnion> s2CellUnionMapViaToken = new HashMap<>();

    private void createS2polygon() {
        try {
            //read taiwan geometry json
            FileInputStream fis = new FileInputStream("src/main/resources/taiwan.geojson");

            String content = new BufferedReader(
                    new InputStreamReader(fis, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONObject jsonObject = new JSONObject(content);
            JSONArray features = jsonObject.getJSONArray("features");

            //create city polygon
            for (int i = 0; i < features.length(); i++) {
                String cityName = features.getJSONObject(i).getJSONObject("properties").getString("名稱");

                //get coordinates
                JSONArray coordinates = features.getJSONObject(i).getJSONObject("geometry")
                        .getJSONArray("coordinates");

                S2PolygonBuilder builder = new S2PolygonBuilder();
                for (int loop_index = 0; loop_index < coordinates.length(); loop_index++) {
                    //get each loop
                    int coordinateSetLength = coordinates.getJSONArray(loop_index).length();

                    for (int coordinateSetIndex = 0; coordinateSetIndex < coordinateSetLength; coordinateSetIndex++) {
                        //get loop each coordinate set
                        JSONArray coordinatesArray = coordinates.getJSONArray(loop_index).getJSONArray(coordinateSetIndex);

                        //[Important] coordinate need set for counter clockwise
                        List<S2Point> s2PointList = new ArrayList<>();
                        for (int coordinateIndex = coordinatesArray.length() - 1; coordinateIndex >= 0; coordinateIndex--) {
                            s2PointList.add(S2LatLng.fromDegrees(coordinatesArray.getJSONArray(coordinateIndex).getDouble(1), coordinatesArray.getJSONArray(coordinateIndex).getDouble(0)).toPoint());
                        }
                        S2Loop s2Loop = new S2Loop(s2PointList);
                        builder.addLoop(s2Loop);
                    }
                }
                //assemble loop
                s2PolygonMap.put(cityName, builder.assemblePolygon());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createS2CellUnion() {
        for (String city : s2PolygonMap.keySet()) {
            //polygon transfer to cell list
            ArrayList<S2CellId> s2CellIds = transferRegion2Cell(s2PolygonMap.get(city));

            //get cellUnion from cell list
            S2CellUnion s2CellUnion = getS2CellUnion(s2CellIds);

            s2CellUnionMap.put(city, s2CellUnion);
        }
    }

    private void createS2CellUnionViaToken() {
        try {
            //read taiwan geometry json
            FileInputStream fis = new FileInputStream("src/main/resources/taiwan_cell.json");

            String content = new BufferedReader(
                    new InputStreamReader(fis, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            JSONArray cityTokenArray = new JSONArray(content);

            for (int index = 0 ; index < cityTokenArray.length() ; index++) {
                JSONObject cityData = cityTokenArray.getJSONObject(index);
                Iterator cityIterator = cityData.keys();
                String cityName = "";
				while (cityIterator.hasNext()) {
                    cityName = (String) cityIterator.next();
					break;
				}

                JSONArray tokens = cityData.getJSONArray(cityName);
                ArrayList<S2CellId> s2CellIdList = new ArrayList<>();
                for (int tokenIndex = 0 ; tokenIndex < tokens.length() ; tokenIndex++) {
                    s2CellIdList.add(S2CellId.fromToken(tokens.getString(tokenIndex)));
                }

                S2CellUnion s2CellUnion = new S2CellUnion();
                s2CellUnion.initFromCellIds(s2CellIdList);
                s2CellUnionMapViaToken.put(cityName, s2CellUnion);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<S2CellId> transferRegion2Cell(S2Region region) {
        S2RegionCoverer coverer = new S2RegionCoverer();
        //max cell level
        coverer.setMaxLevel(20);
        //min cell level
        coverer.setMinLevel(1);
        //max cell count
        coverer.setMaxCells(1000);

        return coverer.getCovering(region).cellIds();
    }

    private S2CellUnion getS2CellUnion(ArrayList<S2CellId> s2CellIds) {
        S2CellUnion s2CellUnion = new S2CellUnion();
        s2CellUnion.initFromCellIds(s2CellIds);

        return s2CellUnion;
    }



    private String getCityWithPolygon(S2Point s2Point) {
        for (String city : s2PolygonMap.keySet()) {
            if (s2PolygonMap.get(city).contains(s2Point)) {
                return city;
            }
        }
        return "CITY NOT FOUND";
    }

    private String getCityWithCellUnion(S2Point s2Point) {
        for (String city : s2CellUnionMap.keySet()) {
            if (s2CellUnionMap.get(city).contains(s2Point)) {
                return city;
            }
        }
        return "CITY NOT FOUND";
    }

    private String getCityWithCellUnionViaToken(S2Point s2Point) {

        for (String city : s2CellUnionMapViaToken.keySet()) {
            if (s2CellUnionMapViaToken.get(city).contains(s2Point)) {
                return city;
            }
        }
        return "CITY NOT FOUND";
    }


    public static void main(String[] args) {
        boolean doCheck = true;

        geometryChecker checker = new geometryChecker();

//        //get s2 polygon
        checker.createS2polygon();
//
//        //create s2 cell union
//        checker.createS2CellUnion();
//
//
//        //s2Cell also can transfer to cell token
//        List<String> tokenList = new ArrayList<>();
//        for (S2CellId s2CellId : checker.transferRegion2Cell(s2PolygonMap.get("臺北市"))) {
//            tokenList.add(s2CellId.toToken());
//        }

        //create s2 cell union via token
        checker.createS2CellUnionViaToken();



        if (doCheck) {
            checkPoint[] checkPoints = checkPoint.values();

            for (checkPoint checkPoint : checkPoints) {
                S2Point s2Point = S2LatLng.fromDegrees(checkPoint.getLat(), checkPoint.getLng()).toPoint();

                System.out.println("CITY POINT : " + checkPoint + " , POLYGON CHECK : " + checker.getCityWithPolygon(s2Point) + " , CELLUNION CHECK : " + checker.getCityWithCellUnionViaToken(s2Point) + ", IS SAME : " + checker.getCityWithPolygon(s2Point).equals(checker.getCityWithCellUnionViaToken(s2Point)));

            }
        }



    }
}
