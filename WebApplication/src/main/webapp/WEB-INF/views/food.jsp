<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script type="text/javascript" src="/resources/food/jquery-1.12.4.js"></script>
    <script type="text/javascript" src="/resources/food/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="/resources/food/dataTables.bootstrap.min.js"></script>

    <link type="text/css" rel="stylesheet" href="/resources/food/bootstrap.min.css"/>
    <link type="text/css" rel="stylesheet" href="/resources/food/dataTables.bootstrap.min.css"/>

    <script>
        $(document).ready(function () {
            $('#example').DataTable({
                "order": [[1, "desc"]]
            });
        });
    </script>
</head>

<body>
<div style="margin: 25px 50px" ; width="80%">Hello world!</div>
<div style="margin: 25px 50px" ; width="80%">
    <table id="example" class="table table-striped table-bordered" cellspacing="0">
        <thead>
        <tr>
            <th>Area GoogleMap Link (ogc_fid)</th>
            <th>Total Tweets</th>
            <th>Food Relevant Tweets</th>
            <th>Positive Sentiment Tweets</th>
            <th>Employees</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="result" items="${results}">
            <tr>
                <td><a href="/map/food/mel/${result.ogcFid}"><c:out value="${result.ogcFid}"/></a></td>
                <td><c:out value="${result.totalTweets}"/></td>
                <td><c:out value="${result.relevantTweets}"/></td>
                <td><c:out value="${result.positiveTweets}"/></td>
                <td><c:out value="${result.employees}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
