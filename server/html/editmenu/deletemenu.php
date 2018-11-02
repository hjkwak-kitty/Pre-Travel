<html>
<head><title> delete menu </title></head>

<body>

<form name="edit" method="POST" action="editmenu.php">

<?php
$connect = mysql_connect("localhost","root","letmesee55!");
$db=mysql_select_db("album",$connect);

$country= $_POST["country"];
$dir = "/var/www/html/img/".$country;

$sql="delete from album where id='$country'";
mysql_query($sql,$connect);
echo exec("rmdir $dir");
$sql="delete from photo where album='$country'";
mysql_query($sql,$connect);


if($sql) echo "Delete Success<br>";
else echo "Delete Failed<br>";
echo "<hr>";
?>
<input type="submit" value="Back">

</form>
</body>
</html>
