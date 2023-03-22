import requests


def main():
    req = requests.get('https://www.apache.org/dyn/closer.cgi?filename=/activemq/5.17.2/apache-activemq-5.17.2-bin.tar.gz&action=download', stream=True)
    f = open('./activemq.tar.gz', 'wb')
    total_copied = 0
    for chunk in req.iter_content(chunk_size=1024*1024):
        if chunk:
            f.write(chunk)
            total_copied += len(chunk)
    f.close()
    req.close()
    req = requests.get('https://download.java.net/java/GA/jdk18/43f95e8614114aeaa8e8a5fcf20a682d/36/GPL/openjdk-18_linux-x64_bin.tar.gz', stream=True)
    f = open('./jre.tar.gz', 'wb')
    total_copied = 0
    for chunk in req.iter_content(chunk_size=1024*1024):
        if chunk:
            f.write(chunk)
            total_copied += len(chunk)
    f.close()


if __name__ == '__main__':
    main()
