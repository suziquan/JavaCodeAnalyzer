package edu.nju.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class FileUtil {

	public static String uniformPathSeparator(String path) {
		String result = path;
		String[] separators = { "\\", "//", "/" };
		for (String separator : separators) {
			if (!File.separator.equals(separator)) {
				result = result.replace(separator, File.separator);
			}
		}
		return result;
	}

	public static String jointPath(String... parts) {
		String path = "";
		for (String part : parts) {
			String toJoint = uniformPathSeparator(part);
			if (toJoint.startsWith(File.separator)) {
				toJoint = toJoint.substring(1, toJoint.length());
			}
			if (!path.endsWith(File.separator) && !path.trim().equals("")) {
				path += File.separator;
			}
			path = path + toJoint;
		}
		return path;
	}

	public static String read(String filePath) {
		File file = new File(filePath);
		return read(file);
	}

	public static String read(String filePath,String encoding) {
		File file = new File(filePath);
		return read(file,encoding);
	}
	
	public static String read(File file){
		return read(file,"utf-8");
	}
	
	public static String read(File file,String encoding) {
		String content = "";
		BufferedReader bufferedReader = null;
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), encoding);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content += line;
				content += System.getProperty("line.separator");
			}
		} catch (Exception e) {
		} finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return content;
	}

}
