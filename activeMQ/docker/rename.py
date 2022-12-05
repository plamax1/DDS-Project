import os
import shutil


def main():
    files = os.listdir()
    active_mq_files = [f for f in files if 'activemq' in f]
    active_mq_file = active_mq_files[0] if len(active_mq_files) > 0 else None
    jre_files = [f for f in files if 'jdk' in f]
    jre_file = jre_files[0] if len(jre_files) > 0 else None
    if active_mq_file is not None:
        shutil.move(active_mq_file, 'activemq')
    if jre_file is not None:
        shutil.move(jre_file, 'jdk')


if __name__ == '__main__':
    main()
