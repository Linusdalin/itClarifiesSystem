<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />

    <title>Static 3</title>

      <style type="text/css">
        @import "/bo/adminCommon/styles/table/tablepage.css";
      </style>
      <script src="http://code.jquery.com/jquery-1.7.min.js"></script>

      <script class="jsbin" src="http://datatables.net/download/build/jquery.dataTables.nightly.js"></script>

      <script>
      $(document).ready(function(){
      	$('#example').dataTable();
      });
      </script>

  </head>
  <body>

  <div id="dt_example">
    <div id="container">
        <h1> Table: </h1>
        <table cellpadding="0" cellspacing="0" border="0" class="display" id="example">
            <thead>
                <tr>
                    <th>Id</th>
                    <th> User</th>
                    <th> Location</th>
                    <th> Time</th>
                    <th> Birth</th>
                    <th> Occurrences</th>
                </tr>
            </thead>
            <tbody>

                <tr class=odd>
                    <td>1</td>
                    <td>Player1</td>
                    <td>Ankeborg</td>
                    <td>2012-09-30 12:25:20.0</td>
                    <td>2010-12-10</td>
                    <td>5</td>

                </tr>
                <tr >
                    <td>2</td>

                    <td>Player1</td>
                    <td>Other Place</td>
                    <td>2012-09-30 12:25:20.0</td>
                    <td>1987-10-10</td>
                    <td>1</td>

                </tr>

            </tbody>
        </table>
    </div>
</div>
</body>

</html>

