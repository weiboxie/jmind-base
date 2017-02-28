package jmind.base.op;

import jmind.base.util.IOUtil;

public abstract class Task {

    public String getPath() {
        return this.getClass().getSimpleName();
    }

    public abstract Object doIt(String mark);

    private void setMark(Object mark) {
        if (mark != null)
            IOUtil.writerText(getPath(), mark, false);
    }

    public String getMark() {
        return IOUtil.readerFile(getPath());
    }

    public void doTask() {
        String mark = getMark();
        Object oMark = null;
        do {
            oMark = doIt(mark);
            setMark(oMark);
        } while (oMark != null);
    }

}
