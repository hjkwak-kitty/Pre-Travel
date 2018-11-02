<html>
<head><title> delete photo </title></head>

<body>

<form name="edit" method="POST" action="list.php?country=$country">

<?php

$connect = mysql_connect("localhost","root","letmesee55!");
$db=mysql_select_db("album",$connect);

$country= $_GET["country"];
$title=$_GET["title"];
$where = "/var/www/html/img/".$country."/".$title;

$sql="delete from photo where album='$country' and title='$title'";
mysql_query($sql,$connect);
echo exec("rm -rf $where");

if($sql) echo "Delete Success<br>";
else echo "Delete Failed<br>";
echo "<hr>";
?>
<input type="submit" value="Back">

</form>
</body>
</html>


