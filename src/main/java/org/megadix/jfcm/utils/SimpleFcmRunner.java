/*
JFCM (Java Fuzzy Congnitive Maps)
Copyright (C) De Franciscis Dimitri - www.megadix.it

This library is free software; you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation; either version 2.1 of the License, or (at your option) any
later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along
with this library; if not, write to the Free Software Foundation, Inc., 59
Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.megadix.jfcm.utils;

import org.megadix.jfcm.CognitiveMap;
import org.megadix.jfcm.Concept;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple implementation of {@link org.megadix.jfcm.utils.FcmRunner} that permits iterative
 * execution of the map, CSV output and a simple convergence-detection mechanism.
 */
public class SimpleFcmRunner extends BaseFcmRunner {

    private double maxDelta;

    // CSV output stuff
    private String csvOutputFileName;
    private Charset csvOutputCharset = Charset.forName("UTF-8");
    private File csvOutputFile;
    private OutputStream csvOutputStream;
    private BufferedWriter csvWriter;

    public SimpleFcmRunner() {
    }

    public SimpleFcmRunner(CognitiveMap map, int maxEpochs) {
        super(map, maxEpochs);
    }

    public SimpleFcmRunner(double maxDelta, int maxEpochs) {
        super(maxEpochs);
        this.maxDelta = maxDelta;
    }

    public SimpleFcmRunner(CognitiveMap map, double maxDelta, int maxEpochs) {
        super(map, maxEpochs);
        this.maxDelta = maxDelta;
    }

    /**
     * When {@link org.megadix.jfcm.CognitiveMap#getAverageSquareDelta()} &gt; maxDelta, computation stops.
     *
     * @param maxDelta max average square delta
     */
    public void setMaxDelta(double maxDelta) {
        this.maxDelta = maxDelta;
    }

    /**
     * File to output CSV values to, default charset (UTF-8)
     *
     * @param csvOutputFile path of csv output file
     */
    public void setCsvOutputFile(String csvOutputFile) {
        this.csvOutputFileName = csvOutputFile;
        this.csvOutputCharset = Charset.forName("UTF-8");
    }

    /**
     * File to output CSV values to.
     *
     * @param csvOutputFile path of csv output file
     * @param charset       name of the charset to use
     */
    public void setCsvOutputFile(String csvOutputFile, String charset) {
        this.csvOutputFileName = csvOutputFile;
        this.csvOutputCharset = Charset.forName(charset);
    }

    public boolean converge() {
        if (map == null) {
            throw new IllegalStateException("map == null");
        }

        Double delta;

        try {
            beforeRun();

            delta = map.calculateAverageSquareDelta();
            int i = 0;
            while ((delta == null || Double.isNaN(delta) || Double.isInfinite(delta) || delta > maxDelta) && i < maxEpochs) {
                map.execute();
                delta = map.calculateAverageSquareDelta();
                writeOutputs(i + 1);
                i++;
            }

            afterRun();

        } catch (Exception ex) {
            throw new RuntimeException("Error running map", ex);
        }

        return (delta != null && delta <= maxDelta);
    }

    public void run() {
        if (map == null) {
            throw new IllegalStateException("map == null");
        }

        try {
            beforeRun();

            // main loop
            for (int i = 0; i < maxEpochs; i++) {
                map.execute();
                writeOutputs(i + 1);
            }

            afterRun();

        } catch (Exception ex) {
            throw new RuntimeException("Error running map", ex);
        }
    }

    protected void beforeRun() throws IOException {
        // write headers
        writeHeaders();
        // write initial map state
        writeOutputs(0);
    }

    protected void afterRun() throws IOException {
        // close file
        closeCsvFile();
    }

    /**
     * Create csvOutputFile
     *
     * @return <code>true</code> if new file, <code>false otherwise</code>
     * @throws IOException
     */
    private boolean openOutputFile() throws IOException {
        boolean created = false;
        Path path = Paths.get(csvOutputFileName);
        csvOutputFile = path.toFile();

        if (csvOutputFile.exists()) {
            if (csvOutputFile.isDirectory()) {
                throw new IllegalArgumentException("CSV output \"" + path.toAbsolutePath() + "\" is a directory");
            } else if (!csvOutputFile.canWrite()) {
                throw new IllegalArgumentException("Cannot write to CSV output file: \"" + path.toAbsolutePath());
            }
            // delete old file
            if (!csvOutputFile.delete()) {
                throw new IOException("Cannot delete file: " + path.toAbsolutePath());
            }
            // re-create file
            created = csvOutputFile.createNewFile();
            if (!created) {
                throw new IOException("Cannot create file: " + path.toAbsolutePath());
            }

        } else {
            // create parent directory if necessary
            if (!csvOutputFile.getParentFile().exists()) {
                csvOutputFile.getParentFile().mkdirs();
            }
            // create file
            created = csvOutputFile.createNewFile();
            if (!created) {
                throw new IOException("Cannot create file: " + path.toAbsolutePath());
            }
        }

        csvOutputStream = new BufferedOutputStream(new FileOutputStream(csvOutputFile));
        csvWriter = new BufferedWriter(new OutputStreamWriter(csvOutputStream, csvOutputCharset));

        return created;
    }

    private void closeCsvFile() throws IOException {
        if (csvOutputFile == null) {
            return;
        }
        csvWriter.close();
        csvOutputStream.close();
    }

    private void writeHeaders() throws IOException {
        if (csvOutputFileName == null) {
            return;
        }

        boolean newFile = openOutputFile();
        if (newFile) {
            csvWriter.write("\"iteration\"");

            for (Concept c : map.getConcepts().values()) {
                csvWriter.write(",");
                String name = c.getName().replaceAll("\"", "\"\"");
                csvWriter.write("\"" + name + "\"");
            }
            csvWriter.write("\n");
        }
    }

    private void writeOutputs(int iteration) throws IOException {
        if (csvOutputFile == null) {
            return;
        }

        csvWriter.write(Integer.toString(iteration));

        for (Concept c : map.getConcepts().values()) {
            csvWriter.write(",");
            if (c.getOutput() != null && !c.getOutput().isNaN() && !c.getOutput().isInfinite()) {
                csvWriter.write(c.getOutput().toString());
            }
        }
        csvWriter.write("\n");
    }
}
