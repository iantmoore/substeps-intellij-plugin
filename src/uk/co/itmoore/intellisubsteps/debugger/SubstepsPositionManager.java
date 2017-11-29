package uk.co.itmoore.intellisubsteps.debugger;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.sun.jdi.*;

import java.util.ArrayList;
import java.util.List;

public class SubstepsPositionManager implements PositionManager {

    private static final Logger log = LogManager.getLogger(SubstepsPositionManager.class);


    @Nullable
    @Override
    public SourcePosition getSourcePosition(@Nullable Location location) throws NoDataException {

        log.debug("getSourcePosition");

        return null;

    }

    @NotNull
    @Override
    public List<ReferenceType> getAllClasses(@NotNull SourcePosition sourcePosition) throws NoDataException {

        log.debug("getAllClasses");
        return (List<ReferenceType>) new ArrayList<ReferenceType>();
    }

    @Nullable
    @Override
    public ClassPrepareRequest createPrepareRequest(@NotNull ClassPrepareRequestor classPrepareRequestor, @NotNull SourcePosition sourcePosition) throws NoDataException {
        log.debug("createPrepareRequest");


        return null;
    }

    @NotNull
    @Override
    public List<Location> locationsOfLine(@NotNull ReferenceType referenceType, @NotNull SourcePosition sourcePosition) throws NoDataException {

        log.debug("locationsOfLine");
        return new ArrayList<Location>();
    }
}
