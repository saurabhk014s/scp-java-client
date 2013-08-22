@echo off
del ..\..\..\log\scp.log
..\..\..\scp.cmd -file "*.cmd" -todir "rattom:@192.168.1.4:/home/rattom/tmp" -keyfile "certificato_Putty.ppk" -trust true
