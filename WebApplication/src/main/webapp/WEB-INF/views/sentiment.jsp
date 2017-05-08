<!-- abnormal in firefox -->
<html>
<head>
    <style>
        body {
            font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
            margin: auto;
            padding-top: 40px;
            position: relative;
            width: 960px;
        }

        button {
            position: absolute;
            right: 10px;
            top: 10px;
        }

        .bullet {
            font: 10px sans-serif;
        }

        /* .bullet .marker { stroke: #000; stroke-width: 2px; } */
        .bullet .tick line {
            stroke: #666;
            stroke-width: .5px;
        }

        .bullet .range.s0 {
            fill: #eee;
        }

        .bullet .range.s1 {
            fill: #ddd;
        }

        .bullet .range.s2 {
            fill: #ccc;
        }

        .bullet .measure.s0 {
            fill: steelblue;
        }

        /*
        .bullet .measure.s1 {
            fill: lightsteelblue;
        }
        */

        .bullet .title {
            font-size: 14px;
            font-weight: bold;
        }

        .bullet .subtitle {
            fill: #999;
        }
    </style>

    <style>
        .liquidFillGaugeText {
            font-family: Helvetica;
            font-weight: bold;
        }
    </style>

    <script src="/resources/sentiment/d3.v3.min.js"></script>
    <script src="/resources/sentiment/bullet.js"></script>
    <script src="/resources/sentiment/jquery.min.js"></script>
    <script src="/resources/sentiment/liquidFillGauge.js" language="JavaScript"></script>
</head>

<body>
    <div style="width:50%;margin: 0 auto;text-align: center;">How many POSITIVE tweets are there in Melbourne & Sydney?</div>
    <button id="btn01">Update</button>
    <div style="width:80%;margin: 0 auto;text-align: center;">
        <table style="margin: 0 auto;">
            <tr height="200">
                <td>
                    <p width="300">Melbourne</p>
                    <p id="melPos">Positive : </p>
                    <p id="melTotal">Total : </p>
                </td>
                <td>
                    <svg id="fillgauge1"></svg>
                </td>
                <td>
                    <p width="300">Sydney</p>
                    <p id="sydPos">Positive : </p>
                    <p id="sydTotal">Total : </p>
                </td>
                <td>
                    <svg id="fillgauge2"></svg>
                </td>
            </tr>
        </table>
    </div>
    <div style="width:80%;margin: 20px auto;text-align: center;">How many POSITIVE tweets are there in each weekday in (Melbourne + Sydney)?</div>
    <div id="barchart"></div>

    <script>
        var margin = {top: 5, right: 40, bottom: 20, left: 120},
            width = 960 - margin.left - margin.right,
            height = 50 - margin.top - margin.bottom;

        var chart = d3.bullet()
            .width(width)
            .height(height);

        /*
         measures: how many tweets are positive
         ranges: how many tweets in total!
         */
        var dataJson = [
            {
                "title": "Monday",
                "subtitle": "",
                "ranges": [0, 0, 0],
                "measures": [0],
                "markers": [0]
            },
            {
                "title": "Tuesday",
                "subtitle": "",
                "ranges": [0, 0, 0],
                "measures": [0],
                "markers": [0]
            },
            {
                "title": "Wednesday",
                "subtitle": "",
                "ranges": [0, 0, 0],
                "measures": [0],
                "markers": [0]
            },
            {
                "title": "Thursday",
                "subtitle": "",
                "ranges": [0, 0, 0],
                "measures": [0],
                "markers": [0]
            },
            {
                "title": "Friday",
                "subtitle": "",
                "ranges": [0, 0, 0],
                "measures": [0],
                "markers": [0]
            },
            {
                "title": "Saturday",
                "subtitle": "",
                "ranges": [0, 0, 0],
                "measures": [0],
                "markers": [0]
            },
            {
                "title": "Sunday",
                "subtitle": "",
                "ranges": [0, 0, 0],
                "measures": [0],
                "markers": [0]
            }


        ];


        var svg = d3.select("#barchart").selectAll("svg")
            .data(dataJson)
            .enter().append("svg")
            .attr("class", "bullet")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
            .call(chart);

        var title = svg.append("g")
            .style("text-anchor", "end")
            .attr("transform", "translate(-6," + height / 2 + ")");

        title.append("text")
            .attr("class", "title")
            .text(function (d) {
                return d.title;
            });

        title.append("text")
            .attr("class", "subtitle")
            .attr("dy", "1em")
            .text(function (d) {
                return d.subtitle;
            });

        d3.selectAll("button").on("click", function () {
            $.ajax({
                type: "GET",
                url: "/sentiment/meter/json"
            }).done(function (data) {
                gauge1.update(data.melbourneRatio);
                gauge2.update(data.sydneyRatio);

            });

        });

        d3.selectAll("button").on("click", function () {
            $.ajax({
                type: "GET",
                url: "/sentiment/meter/json"
            }).done(function (data) {
                gauge1.update(data.melbourneRatio);
                gauge2.update(data.sydneyRatio);
                $('#melPos').text('Positive tweets: ' + data.melbournePositive);
                $('#melTotal').text('Total tweets: ' + data.melbourneTotal);
                $('#sydPos').text('Positive tweets: ' + data.sydneyPositive);
                $('#sydTotal').text('Total tweets: ' + data.sydneyTotal);
            });

            $.ajax({
                type: "GET",
                url: "/sentiment/bar/json"
            }).done(function (data) {
                svg.data(data).call(chart.duration(1000)); // TODO automatic transition

            });

            $('#btn01').text('Last Updated: ' + new Date().toTimeString());
        });

    </script>

    <script language="JavaScript">
        var config1 = liquidFillGaugeDefaultSettings();
        config1.circleThickness = 0.1;
        config1.waveAnimateTime = 1000;
        var gauge1 = loadLiquidFillGauge("fillgauge1", 0, config1);
        var config2 = liquidFillGaugeDefaultSettings();
        config2.circleColor = "#FF7777";
        config2.textColor = "#FF4444";
        config2.waveTextColor = "#FFAAAA";
        config2.waveColor = "#FFDDDD";
        config2.circleThickness = 0.1;
        config2.waveAnimateTime = 1000;
        var gauge2 = loadLiquidFillGauge("fillgauge2", 0, config2);
    </script>
</body>
</html>
