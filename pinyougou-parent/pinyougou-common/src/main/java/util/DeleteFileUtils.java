package util;

import java.io.IOException;
import java.io.InputStream;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class DeleteFileUtils {
	public static void main(String[] args) throws IOException {
		String group = "group1";
		String filePath = "M00/00/00/wKgZhVtpYnCACdG7AAr1E0GGdVs033.jpg";
		 //String filePath ="M00/00/00/wKgZhVtpYw-ELgvcAAAAAOjkUwA514.jpg";
		//deleteFile(group, filePath);
		System.out.println("shuru");
		int read = System.in.read();
		System.out.println(read);
		
		
	}

	public static void deleteFile(String group, String filePath) {

		TrackerServer trackerServer = null;
		StorageServer storageServer = null;

		// ����fastdfs�ͻ��ˣ�ʵ���ļ��ϴ���fastdfs��������
		try {
			// ������ģ��ʽ��
			// 1�����������ļ�
			ClientGlobal.init("D:\\fdfs_client.conf");

			// 2������һ��tracker�Ŀͻ���
			TrackerClient trackerClient = new TrackerClient();

			// 3��ͨ��trackerClient��ȡһ�����ӣ����ӵ�Tracker���õ�һ��TrackerServer
			trackerServer = trackerClient.getConnection();

			// 4��ͨ��trackerClient��ȡһ���洢�ڵ��StorageServer
			storageServer = trackerClient.getStoreStorage(trackerServer);

			// 5��ͨ��trackerServer��storageServer����һ��Storage�ͻ���
			StorageClient storageClient = new StorageClient(trackerServer, storageServer);

			// fastdfsɾ���ļ�
			int delete_file = storageClient.delete_file(group, filePath);
			System.out.println(delete_file);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("-2");
		} finally {
			try {
				// �رշ����ͷ���Դ
				if (null != storageServer) {
					storageServer.close();
				}
				if (null != trackerServer) {
					trackerServer.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
