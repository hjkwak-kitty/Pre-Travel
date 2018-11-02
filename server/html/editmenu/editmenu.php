<html>
<head><title>edit menu</title></head>

<body>	
        <?php
	session_start();
	if(!isset($_SESSION['user_id']) || !isset($_SESSION['user_name'])){
	echo "<meta http-equiv='refresh' content='0;url=login.php'>";
	exit;
	}
	$user_id = $_SESSION['user_id'];
	$user_name= $_SESSION['user_name'];
	echo "<p>안녕하세요. $user_name($user_id)님</p>";
	
        $connect =mysql_connect('localhost','root','letmesee55!');
        $db=mysql_select_db("album",$connect);
        $sql ="select * from album";
        $sql_result=mysql_query($sql,$connect);
        $count = mysql_num_rows($sql_result);
	?>
	
 	<a href='logout.php'>로그아웃</a>
 	
	<form name="delete" id="delete" method="post" action="deletemenu.php">
	<hr>
	<?php
       	echo "삭제: ";
	 ?>

        <select id="selectCountry" name="country" onchange="setValues();">
        <option value="" selected>select country</option>
        <?php
        for($i=0;$i<$count;$i++){
        $image_num=mysql_result($sql_result,$i,image_num);
        $id=mysql_result($sql_result,$i,id);
        echo("<option value='{$id}'>{$id}</option>");        
	}?>
        </select>
	<td><input type="submit" value="delete"></td>
	</form>

	<tr><tr>
	<form name="add" id="add" method="POST" action="addmenu.php">
	<?php echo "추가: "; ?>
	<input type="text" name="new" value="new album name"/>
	<input type="submit" name="add" id="add" value="add"/>	
	</form>
        
	<form name="update" id="update"  method="POST" action="updatemenu.php">
	<?php echo "수정: ";?>
	<select id="selectCountry" name="country" onchange="setValues();">
        <option value="" selected>select country</option>
        <?php
        for($i=0;$i<$count;$i++){
        $image_num=mysql_result($sql_result,$i,image_num);
        $id=mysql_result($sql_result,$i,id);
        echo("<option value='{$id}'>{$id}</option>");
        }?>
        </select>
	<input type="text" name="new" value="new album name"/>
	<input type="submit" name="update" id="update" value="update"/>
	</form>
	
	<form name="finish" id="finish" method="POST" action="../index.php">	
	</tr>
	<br>
	<td><td><td><tr>
	<input type="submit" name="finish" id="finish" value="완료"/>
	</form>

</body>
</html>
