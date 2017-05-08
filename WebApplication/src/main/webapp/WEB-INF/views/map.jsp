<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <style>
        /* Always set the map height explicitly to define the size of the div
         * element that contains the map. */
        #map {
            height: 100%;
        }

        /* Optional: Makes the sample page fill the window. */
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
        }
    </style>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDcFEs8fcjtmfcmS_l42oKr_RJpW4N_nwI"></script>
    <script>

        function initialize() {
            var coordinates = ${jsonArray};

            var mapOptions = {
                zoom: 16,
                center: new google.maps.LatLng(coordinates[0][0][0][1], coordinates[0][0][0][0]),//initial location
                mapTypeId: 'terrain'
            };

            //create the map
            map = new google.maps.Map(document.getElementById('map'), mapOptions);

            // Put polygons on the map!
            for (var i = 0; i < coordinates.length; i++) {
                for (var j = 0; j < coordinates[i].length; j++) {
                    var coordinatesArray = [];
                    for (var k = 0; k < coordinates[i][j].length; k++) {
                        coordinatesArray[k] = new google.maps.LatLng(coordinates[i][j][k][1], coordinates[i][j][k][0]);
                    }
                    var area = new google.maps.Polyline({
                        path: coordinatesArray,
                        editable: false,
                        strokeColor: '#FF0000',
                        strokeOpacity: 1,
                        strokeWeight: 3
                    });
                    area.setMap(map);
                }
            }
        }
        google.maps.event.addDomListener(window, 'load', initialize);
    </script>
</head>

<body>
<div id="map"></div>
</body>
</html>