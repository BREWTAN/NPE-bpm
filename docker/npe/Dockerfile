FROM tosjava
MAINTAINER  Tany
######################
ADD fsm.tar.gz /usr/local/npe/fsm
ADD mcs.tar.gz /usr/local/npe/mcs
ADD www.tar.gz /usr/local/npe/
ADD envset.sh /usr/bin/envset.sh
ENV DB_URL 	fcpay:fcpayment@192.168.10.229:3306/npe

##################
# change settings.
ADD supervisord.conf /etc/supervisord.conf
EXPOSE 22 3306 2501 2600 8080 9000
CMD ["/usr/bin/supervisord"]





