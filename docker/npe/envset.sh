#!/bin/bash

if [ ! -f /etc/firstrun ]; then 

    echo "$DB_URL" >> /etc/firstrun
  ###ENV DB_URL mysql://username:password@ip:port/name

  dbarray=(`sed -e 's/:/ /g;s/:/ /g;s/@/ /g;s/\// /g' <<<$DB_URL`)

  export DB_USER=${dbarray[0]}
  export DB_PASS=${dbarray[1]}
  export DB_HOST=${dbarray[2]}
  export DB_PORT=${dbarray[3]}
  export DB_NAME=${dbarray[4]}

  sed -i -e "s/db.username/db.username=$DB_USER#/g" -e "s/db.password/db.password=$DB_PASS#/g" \
      -e "s/db.port/db.port=$DB_PORT#/g" -e "s/db.name/db.name=$DB_NAME#/g" \
     -e "s/db.host/db.host=$DB_HOST#/g" /usr/local/npe/fsm/conf/application.conf

  sed -i -e "s/db.username/db.username=$DB_USER#/g" -e "s/db.password/db.password=$DB_PASS#/g" \
      -e "s/db.port/db.port=$DB_PORT#/g" -e "s/db.name/db.name=$DB_NAME#/g" \
     -e "s/db.host/db.host=$DB_HOST#/g" /usr/local/npe/mcs/conf/application.conf

   echo -e "\n export DB_URL=\"$DB_URL\" " >> /etc/profile

   echo -e "\n export DB_USER=\"$DB_USER\" " >> /etc/profile
   echo -e "\n export DB_PASS=\"$DB_PASS\" " >> /etc/profile
   echo -e "\n export DB_HOST=\"$DB_HOST\" " >> /etc/profile
   echo -e "\n export DB_NAME=\"$DB_NAME\"" >> /etc/profile
   echo -e "\n export DB_PORT=\"$DB_PORT\" " >> /etc/profile


fi


