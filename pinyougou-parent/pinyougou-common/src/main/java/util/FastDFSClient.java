package util;

import java.io.IOException;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastDFSClient {

	private TrackerClient trackerClient = null;
	private TrackerServer trackerServer = null;
	private StorageServer storageServer = null;
	private StorageClient1 storageClient = null;

	public FastDFSClient(String conf) throws Exception {
		if (conf.contains("classpath:")) {
			conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
		}
		ClientGlobal.init(conf);
		trackerClient = new TrackerClient();
		trackerServer = trackerClient.getConnection();
		storageServer = null;
		storageClient = new StorageClient1(trackerServer, storageServer);
	}

	/**
	 * 上传文件方法
	 * <p>
	 * Title: uploadFile
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param fileName
	 *            文件全路径
	 * @param extName
	 *            文件扩展名，不包含（.）
	 * @param metas
	 *            文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
		String result = storageClient.upload_file1(fileName, extName, metas);
		return result;
	}

	public String uploadFile(String fileName) throws Exception {
		return uploadFile(fileName, null, null);
	}

	public String uploadFile(String fileName, String extName) throws Exception {
		return uploadFile(fileName, extName, null);
	}

	/**
	 * 上传文件方法
	 * <p>
	 * Title: uploadFile
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param fileContent
	 *            文件的内容，字节数组
	 * @param extName
	 *            文件扩展名
	 * @param metas
	 *            文件扩展信息
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

		String result = storageClient.upload_file1(fileContent, extName, metas);
		return result;
	}

	public String uploadFile(byte[] fileContent) throws Exception {
		return uploadFile(fileContent, null, null);
	}

	public String uploadFile(byte[] fileContent, String extName) throws Exception {
		return uploadFile(fileContent, extName, null);
	}

	// 删除图片
	public static void deleteFile(StorageClient storageClient, String group, String fileId)
			throws IOException, Exception {
		int i = storageClient.delete_file(group, fileId);
		System.out.println(i == 0 ? "删除成功" : "删除失败:" + i);

	}

	public  int deleteFile(String group, String findID) {
		try{
			storageClient.delete_file(group, findID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭服务，释放资源
				if (null != storageServer) {
					storageServer.close();
				}
				if (null != trackerServer) {
					trackerServer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
}
