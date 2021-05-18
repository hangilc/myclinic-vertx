stamp=$(date +'%Y%m%d')
sqlfile="myclinic-backup-$stamp.sql"
mysqldump -h $MYCLINIC_DB_HOST -u $MYCLINIC_DB_USER -p$MYCLINIC_DB_PASS --set-gtid-purged=OFF \
	myclinic >/tmp/$sqlfile
gzip /tmp/$sqlfile
aws s3 cp /tmp/$sqlfile.gz s3://$MYCLINIC_DB_S3_BUCKET
rm /tmp/$sqlfile.gz




