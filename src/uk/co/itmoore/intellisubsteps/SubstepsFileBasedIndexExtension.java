package uk.co.itmoore.intellisubsteps;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

import static com.intellij.util.indexing.ScalarIndexExtension.VOID_DATA_EXTERNALIZER;

/**
 * Created by ian on 30/10/16.
 */
public class SubstepsFileBasedIndexExtension extends FileBasedIndexExtension {

    private static final Logger log = LogManager.getLogger(SubstepsFileBasedIndexExtension.class);
    public static final @NonNls ID<String, Void> NAME = ID.create("SubstepsIndex");

    private final MyDataIndexer myDataIndexer = new MyDataIndexer();

    public SubstepsFileBasedIndexExtension(){
        log.debug("SubstepsFileBasedIndexExtension ctor");
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> (file != null && file.getExtension() != null) &&
                (file.getExtension().equalsIgnoreCase("feature") || file.getExtension().equalsIgnoreCase("substeps"));
    }

    @Override
    public boolean dependsOnFileContent() {
        return false;
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return myDataIndexer;
        // TODO
        /*
        interface DataIndexer<Key, Value, Data> {
  @NotNull
  Map<Key,Value> map(@NotNull Data inputData);
}
         */
    }


    private static class MyDataIndexer implements DataIndexer<String, Void, FileContent> {

        private static final Logger log = LogManager.getLogger(MyDataIndexer.class);


        @Override
        @NotNull
        public Map<String, Void> map(@NotNull final FileContent inputData) {

            log.debug("mapping data: " + inputData.getFileName());

            // return a map of the keys that can be found in this file
            log.debug("content: " + inputData.getContentAsText());

            return Collections.singletonMap("mykey", null);
        }
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {

        return EnumeratorStringDescriptor.INSTANCE;

        // KEY - used to retrieve data from the index - ie the bare step strings

        // VALUE - arbitrary data associated with the key in the index - eg, in word index, value = mask for the context in which the value is found

        // DATA - the data to be indexed

        //return null;
    }

    @NotNull
    @Override
    public DataExternalizer<Void> getValueExternalizer() {
        return VOID_DATA_EXTERNALIZER;
    }

    @Override
    public int getVersion() {
        return 0;
    }
}
