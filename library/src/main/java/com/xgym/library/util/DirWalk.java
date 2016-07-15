package com.gwsoft.library.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 根据条件，遍历目录下的文件及目录
 */
public class DirWalk implements Iterable<File> {
    /**
     * 目录过滤器，当该过滤器返回 true 时，则不遍历该目录及其子目录
     */
    private FileFilter dirFilter;
    /**
     * 文件过滤器，当该过滤器返回 true 时，文件被包含在结果中
     */
    private FileFilter fileFilter;

    private List<File> fileList = new ArrayList<File>();
    /**
     * 需遍历的目录路径集合
     */
    private String[] dirs;
    /**
     * 遍历结果中是否忽略目录
     */
    private boolean ignoreDir = false;

    public DirWalk(String... dir) {
        if (dir == null) {
            throw new NullPointerException("未指定目录路径");
        } else {
            this.dirs = dir;
            dirFilter(null).fileFilter(null);
        }
    }

    public DirWalk fileFilter(final FileFilter fileFilter) {
        if (fileFilter != null) {
            this.fileFilter = fileFilter;
        } else {
            this.fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return true;
                }
            };
        }
        return this;
    }

    @Override
    public Iterator<File> iterator() {
        return fileList.iterator();
    }

    public DirWalk dirFilter(final FileFilter dirFilter) {
        if (dirFilter != null) {
            this.dirFilter = dirFilter;
        } else {
            this.dirFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return true;
                }
            };
        }
        return this;
    }

    public DirWalk ignoreDir(final boolean ignoreDir) {
        this.ignoreDir = ignoreDir;
        return this;
    }

    public DirWalk walk() {
        List<File> dirList = new ArrayList<File>();
        for (String dir : dirs) {
            File file = new File(dir);
            if (file.exists() && file.isDirectory() && dirFilter.accept(file)) {
                dirList.add(file);
            }
        }
        while (!dirList.isEmpty()) {
            File file = dirList.remove(0);
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (child.isDirectory()) {
                        if (dirFilter.accept(child)) {
                            dirList.add(child);
                        }
                        if (!ignoreDir && fileFilter.accept(child)) {
                            fileList.add(child);
                        }
                    } else if (fileFilter.accept(child)) {
                        fileList.add(child);
                    }
                }
            }
        }
        return this;
    }

    public List<File> get() {
        return fileList;
    }
}
