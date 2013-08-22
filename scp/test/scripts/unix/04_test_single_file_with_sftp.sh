#!/bin/sh
rm ../../../log/scp.log
../../../scp.sh -file "$0" -todir rattom:@160.220.142.242:/home/rattom/tmp -keyfile /home/rattom/.ssh/id_rsa -trust true -sftp true
