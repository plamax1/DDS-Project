FROM ubuntu

RUN apt-get update -y
RUN apt-get install curl python3 python3-pip libc6-i386 -y
RUN pip3 install requests

COPY ./download.py /var/activemq/download.py

WORKDIR /var/activemq

RUN python3 /var/activemq/download.py

RUN tar -xvz -f ./activemq.tar.gz
RUN rm ./activemq.tar.gz

RUN tar -xvz -f ./jre.tar.gz
RUN rm ./jre.tar.gz

COPY ./rename.py /var/activemq/rename.py

RUN python3 /var/activemq/rename.py

RUN mv jdk /opt/jve/

ENV JAVA_HOME="/opt/jve"
ENV PATH="${PATH}:${JAVA_HOME}/bin"

ENTRYPOINT ["./activemq/bin/activemq", "console"]
