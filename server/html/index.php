<html>
<head><title>My Album</title></head>

<body bgcolor="white" text="black" link="blue" vlink="purple" alink="red" tracingopacity="45">

<table border="0" cellpadding="0" cellspacing="0" width="550" height="280" background="img/album.jpg" align="center">
<tr>
 <td width='550' height='280'></td>
</tr>
</table>

<br><hr>
<form action="list.php" method="get">
<table border="0" cellpadding="0" cellspacing="0" width="100" height="100" align="center">

    <tr>
        <td width="100" height="100">
	
	<?php
	
        $connect =mysql_connect('localhost','root','letmesee55!');
        $db=mysql_select_db("album",$connect);

        $sql ="select * from album";
        $sql_result=mysql_query($sql,$connect);
        $count = mysql_num_rows($sql_result);
	

	echo "<select id='selectAlbum' name='country' onchange='setValues();'>
        <option value='' selected>select album</option>";
	
	for($i=0;$i<$count;$i++){
	$image_num=mysql_result($sql_result,$i,image_num);
        $id=mysql_result($sql_result,$i,id);
	echo("<option value='{$id}'>{$id}</option>");
	mysql_close($connect);
	}?>
	<option value="etc">etc</option>	
	</select>
	<td> <input type="submit" style="margin-left:10px;" value="enter"></td></form>
	<form action="editmenu/editmenu.php"> 
	
	<td> <td><input type="submit" style="margin-left:1px;" value="edit"/>
	
	<script>
    	function setValues() {
        	var sh = document.getElementById("selectCountry");
        	var tt = document.getElementById("textTime");
        	tt.value = sh.options[sh.selectedIndex].text;
   	 }
	</script>
	</tr>
	</td></td>
	
    </tr>
</table>
</form>

</body>
</html>
