package uk.co.marcoratto.file.maketree;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

import uk.co.marcoratto.apache.ssh.Directory;

public class MakeTree {

	private static Logger logger = Logger.getLogger(MakeTree.class);

	private int level = 0;

	public MakeTree(MakeTreeInterface obj) {
		this.target = obj;
	}

	public void searchDirectoryFile(File f) throws MakeTreeException {
		this.searchFile(f, "*.*");
	}

	public void searchFile(File f) throws MakeTreeException {
		this.searchFile(f, "*.*");
	}

	public void searchFile(File f, String pattern) throws MakeTreeException {
		try {
			if (f.isDirectory()) {
				String[] list = f.list();
				for (int i = 0; i < list.length; i++) {
					this.searchFile(new File(f, list[i]), pattern);
				}
			} else {
				this.target.onFileFound(f);
			}
		} catch (Exception e) {
			throw new MakeTreeException(e);
		}
	}

	public void searchDirectoryTree(File f, Directory parentDir) throws MakeTreeException {
		try {
			if (f.isDirectory()) {
				final Directory directory = new Directory(f);
				logger.info("Added directory " + f);
				if (parentDir != null) {
					parentDir.addDirectory(directory);
				}
				String[] list = f.list();
				for (String fileName : list) {
					this.searchDirectoryTree(new File(f, fileName), directory);
				}
			} else {
				assert parentDir != null;
				parentDir.addFile(f);
				logger.info("Added file " + f);
			}
		} catch (Throwable t) {
			throw new MakeTreeException(t);
		}
	}

	public void searchDirectoryFile(File fromDirectory, String pattern,
			boolean recursive) throws MakeTreeException {
		try {
			if (fromDirectory.isDirectory()) {
				this.target.onDirFound(fromDirectory);
				FileFilter fileFilter = new WildcardFileFilter(pattern);
				File[] files = fromDirectory.listFiles(fileFilter);
				for (int i = 0; i < files.length; i++) {
					this.target.onFileFound(files[i]);
				}
				if (recursive) {
					String[] list = fromDirectory.list();
					for (int i = 0; i < list.length; i++) {
						this.searchDirectoryFile(new File(fromDirectory,
								list[i]), pattern, recursive);
					}
				}
			}
		} catch (Exception e) {
			throw new MakeTreeException(e);
		}
	}

	public void searchDirectory(File f) throws MakeTreeException {
		try {
			if (f.isDirectory()) {
				this.target.onDirFound(f);
				String[] list = f.list();
				for (int i = 0; i < list.length; i++) {
					this.searchDirectory(new File(f, list[i]));
				}
			}
		} catch (Throwable t) {
			throw new MakeTreeException(t);
		}
	}

	private MakeTreeInterface target = null;
}
