<html>
<head>

<title>My Album</title>

<script>
function nph_change_img_src(name, nsdoc, rpath)
{
     var img = eval((navigator.appName.indexOf('Netscape', 0) != -1) ? nsdoc+'.'+name : 'document.all.'+name);
     if (name == '')    return;
     if (img) {  img.altsrc = img.src;  img.src = rpath; }
}
</script>
</head>


<body bgcolor="white" text="black" link="blue" vlink="purple" alink="red">
<div>

<?php
 echo "In ";
 echo $_GET[country];
 $arr[1]="img/korea/1.jpg";
?>
    <table border="1" cellspacing="0" width="0" height="0" bordercolordark="white" bordercolorlight="gray">
        <tr>
            <td width="212"><a href="javascript:nph_change_img_src('nph_slide1', 'document', 'img/cambodia/2.jpg');"><img src="https://2.bp.blogspot.com/-KhBCDi1qy-w/VsHY7togN5I/AAAAAAAACEc/i4w5K8YFG84/s1600/IMG_1521.JPG" height="120" width="212" border="0" alt="IMG_1857.jpg"></a></td>
            <td width="212"><a href="javascript:nph_change_img_src('nph_slide1', 'document', 'https://3.bp.blogspot.com/-n6oIOv-95wo/VsHfJ00R4vI/AAAAAAAACEw/-Ay1n3hcfPU/s1600/IMG_1533.JPG');"><img src="https://3.bp.blogspot.com/-n6oIOv-95wo/VsHfJ00R4vI/AAAAAAAACEw/-Ay1n3hcfPU/s1600/IMG_1533.JPG" width="212" height="120" border="0" alt="IMG_1858.jpg"></a></td>
            <td width="212"><a href="javascript:nph_change_img_src('nph_slide1', 'document', 'https://1.bp.blogspot.com/-r-vDnwyexnQ/VsIIiX-bB3I/AAAAAAAACGQ/aA2Gxz0La1E/s1600/IMG_1790.JPG');"><img src="https://1.bp.blogspot.com/-r-vDnwyexnQ/VsIIiX-bB3I/AAAAAAAACGQ/aA2Gxz0La1E/s1600/IMG_1790.JPG" width="212" height="120" border="0" alt="IMG_1859.jpg"></a></td>
<td width="212"><a href="javascript:nph_change_img_src('nph_slide1', 'document', 'https://2.bp.blogspot.com/-_RUMGj9yV9E/VsHpt1HZBUI/AAAAAAAACFU/3yU2n-YSIIE/s1600/IMG_1654.JPG');"><img src="https://2.bp.blogspot.com/-_RUMGj9yV9E/VsHpt1HZBUI/AAAAAAAACFU/3yU2n-YSIIE/s1600/IMG_1654.JPG" width="212" height="120" border="0" alt="IMG_1857.jpg"></a></td>
            <td width="212"><a href="javascript:nph_change_img_src('nph_slide1', 'document', 'https://1.bp.blogspot.com/-qtfFgLZdq2A/VsHpt_uw9jI/AAAAAAAACFY/rS0muAOiQIw/s1600/IMG_1675.JPG');"><img src="https://1.bp.blogspot.com/-qtfFgLZdq2A/VsHpt_uw9jI/AAAAAAAACFY/rS0muAOiQIw/s1600/IMG_1675.JPG" width="212" height="120" border="0" alt="IMG_1858.jpg"></a></td>
            <td width="212"><a href="javascript:nph_change_img_src('nph_slide1', 'document', 'https://4.bp.blogspot.com/-FBHi82zhAWc/VsIIjJ71ukI/AAAAAAAACGU/hNfT9fRPuOM/s1600/IMG_1791.JPG');"><img src="https://4.bp.blogspot.com/-FBHi82zhAWc/VsIIjJ71ukI/AAAAAAAACGU/hNfT9fRPuOM/s1600/IMG_1791.JPG" width="212" height="120" border="0" alt="IMG_1860.jpg"></a></td>
          
        </tr>
    </table>

    <p><img src="img/cambodia/2.jpg" border="0" name="nph_slide1"></p>
</div>

</body>

</html>
