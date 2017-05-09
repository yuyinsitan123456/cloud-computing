<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/food/jquery-1.12.4.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/food/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/food/dataTables.bootstrap.min.js"></script>

    <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/resources/food/bootstrap.min.css"/>
    <link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/resources/food/dataTables.bootstrap.min.css"/>

    <style>
        .nav ul {
            list-style: none;
            background-color: #444;
            text-align: center;
            padding: 0;
            margin: 0;
        }

        .nav li {
            font-family: 'Oswald', sans-serif;
            line-height: 40px;
            height: 30px;
            border-bottom: 1px solid #888;
        }

        .nav a {
            text-decoration: none;
            color: #fff;
            display: block;
            transition: .3s background-color;
        }

        .nav a:hover {
            background-color: #005f5f;
        }

        .nav a.active {
            background-color: #fff;
            color: #444;
            cursor: default;
        }

        @media screen and (min-width: 600px) {
            .nav li {
                width: 150px;
                border-bottom: none;
                height: 30px;
                line-height: 30px;
            }

            .nav li {
                display: inline-block;
                margin-right: -4px;
            }
        }
    </style>
    <script>
        $(document).ready(function () {
            $('#example').DataTable({
                "order": [[1, "desc"]]
            });
        });
    </script>
</head>

<body>
<header>
    <div class="nav">
        <ul>
            <li><a style="font-size: 14px" href="<%=request.getContextPath()%>/sentiment">Sentiment</a></li>
            <li><a style="font-size: 14px" href="<%=request.getContextPath()%>/postcode/mel">Postcode{Mel}</a></li>
            <li><a style="font-size: 14px" href="<%=request.getContextPath()%>/postcode/syd">Postcode{Syd}</a></li>
            <c:choose>
                <c:when test="${state.equals('mel')}">
                    <li><a style="font-size: 14px" class="active" href="<%=request.getContextPath()%>/greenspace/mel">GreenSpace{Mel}</a></li>
                    <li><a style="font-size: 14px" href="<%=request.getContextPath()%>/greenspace/syd">GreenSpace{Syd}</a></li>
                </c:when>
                <c:when test="${state.equals('syd')}">
                    <li><a style="font-size: 14px" href="<%=request.getContextPath()%>/greenspace/mel">GreenSpace{Mel}</a></li>
                    <li><a style="font-size: 14px" class="active" href="<%=request.getContextPath()%>/greenspace/syd">GreenSpace{Syd}</a></li>

                </c:when>
            </c:choose>
            <li><a style="font-size: 14px" href="<%=request.getContextPath()%>/food">Restaurant</a></li>
        </ul>
    </div>
</header>
<div style="margin: 30px 10%; width:80%; text-align: center; font-size:x-large;">How many tweets are there in each green space in ${state}?</div>
<div style="margin: 40px 10%; width:80%;">
    <table id="example" class="table table-striped table-bordered" cellspacing="0">
        <thead>
        <tr>
            <th>GoogleMap Link</th>
            <th>Total Tweets</th>
            <th>Positive Sentiment Tweets</th>
            <th>Positive Sentiment Tweets / Total Tweets</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="result" items="${results}">
            <tr>
                <td><a href="<%=request.getContextPath()%>/map/greenspace/${state}/${result.greenspaceId}"><c:out value="${result.greenspaceId}"/></a></td>
                <td><c:out value="${result.totalTweets}"/></td>
                <td><c:out value="${result.positiveTweets}"/></td>
                <td><c:out value="${result.positiveTweets / result.totalTweets}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
