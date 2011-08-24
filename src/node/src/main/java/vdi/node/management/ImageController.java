package vdi.node.management;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vdi.commons.common.Configuration;

/**
 * Class for controlling ISO-images.
 */
public class ImageController {

	private static ImageController instance = new ImageController();

	private String imagePath;

	/**
	 * Constructor.
	 * 
	 * Loads the image path from the configuration file.
	 */
	public ImageController() {
		imagePath = Configuration.getProperty("node.imagefolder");
	}

	public static ImageController getInstance() {
		return instance;
	}

	/**
	 * @return all available images
	 */
	public List<String> getAvailableImages() {
		File folder = new File(imagePath);
		File[] files = folder.listFiles();

		ArrayList<String> images = new ArrayList<String>();

		for (File file : files) {
			if (file.isFile() && !file.isHidden()) {
				images.add(file.getName());
			}
		}

		return images;
	}

	/**
	 * Returns the image's path.
	 * 
	 * @param imageName
	 *            the image's name
	 * @return the path for the image
	 */
	public String getPathForName(String imageName) {
		return new File(imagePath, imageName).getAbsolutePath();
	}

	/**
	 * Returns the name of a path.
	 * 
	 * @param path
	 *            the path
	 * @return the name
	 */
	public String getNameFromPath(String path) {
		if (path == null) {
			return null;
		}

		return new File(path).getName();
	}

}
