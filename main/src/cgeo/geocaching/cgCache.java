package cgeo.geocaching;

import cgeo.geocaching.cgData.StorageLocation;
import cgeo.geocaching.activity.IAbstractActivity;
import cgeo.geocaching.connector.ConnectorFactory;
import cgeo.geocaching.connector.IConnector;
import cgeo.geocaching.connector.gc.GCBase;
import cgeo.geocaching.connector.gc.GCConnector;
import cgeo.geocaching.connector.gc.Tile;
import cgeo.geocaching.enumerations.CacheSize;
import cgeo.geocaching.enumerations.CacheType;
import cgeo.geocaching.enumerations.LoadFlags.RemoveFlag;
import cgeo.geocaching.enumerations.LogType;
import cgeo.geocaching.enumerations.WaypointType;
import cgeo.geocaching.geopoint.Geopoint;
import cgeo.geocaching.geopoint.GeopointFormatter;
import cgeo.geocaching.geopoint.GeopointParser;
import cgeo.geocaching.utils.CancellableHandler;
import cgeo.geocaching.utils.Log;
import cgeo.geocaching.utils.LogTemplateProvider;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal c:geo representation of a "cache"
 */
public class cgCache implements ICache, IWaypoint {

    private long updated = 0;
    private long detailedUpdate = 0;
    private long visitedDate = 0;
    private int listId = StoredList.TEMPORARY_LIST_ID;
    private boolean detailed = false;
    private String geocode = "";
    private String cacheId = "";
    private String guid = "";
    private CacheType cacheType = CacheType.UNKNOWN;
    private String name = "";
    private Spannable nameSp = null;
    private String owner = "";
    private String ownerReal = "";
    private Date hidden = null;
    private String hint = "";
    private CacheSize size = CacheSize.UNKNOWN;
    private float difficulty = 0;
    private float terrain = 0;
    private Float direction = null;
    private Float distance = null;
    private String latlon = "";
    private String location = "";
    private Geopoint coords = null;
    private boolean reliableLatLon = false;
    private Double elevation = null;
    private String personalNote = null;
    private String shortdesc = "";
    private String description = null;
    private boolean disabled = false;
    private boolean archived = false;
    private boolean premiumMembersOnly = false;
    private boolean found = false;
    private boolean favorite = false;
    private boolean own = false;
    private int favoritePoints = 0;
    private float rating = 0; // valid ratings are larger than zero
    private int votes = 0;
    private float myVote = 0; // valid ratings are larger than zero
    private int inventoryItems = 0;
    private boolean onWatchlist = false;
    private List<String> attributes = null;
    private List<cgWaypoint> waypoints = null;
    private ArrayList<cgImage> spoilers = null;
    private List<cgLog> logs = null;
    private List<cgTrackable> inventory = null;
    private Map<LogType, Integer> logCounts = new HashMap<LogType, Integer>();
    private boolean logOffline = false;
    private boolean userModifiedCoords = false;
    // temporary values
    private boolean statusChecked = false;
    private boolean statusCheckedView = false;
    private String directionImg = "";
    private String nameForSorting;
    private final EnumSet<StorageLocation> storageLocation = EnumSet.of(StorageLocation.HEAP);
    private boolean finalDefined = false;
    private int zoomlevel = Tile.ZOOMLEVEL_MAX;

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    private Handler changeNotificationHandler = null;

    public void setChangeNotificationHandler(Handler newNotificationHandler) {
        changeNotificationHandler = newNotificationHandler;
    }

    /**
     * Sends a change notification to interested parties
     */
    private void notifyChange() {
        if (changeNotificationHandler != null) {
            changeNotificationHandler.sendEmptyMessage(0);
        }
    }

    /**
     * Gather missing information from another cache object.
     *
     * @param other
     *            the other version, or null if non-existent
     * @return true if this cache is "equal" to the other version
     */
    public boolean gatherMissingFrom(final cgCache other) {
        if (other == null) {
            return false;
        }

        updated = System.currentTimeMillis();
        if (!detailed && other.detailed) {
            detailed = true;
            detailedUpdate = other.detailedUpdate;
            coords = other.coords;
            // boolean values must be enumerated here. Other types are assigned outside this if-statement
            premiumMembersOnly = other.premiumMembersOnly;
            reliableLatLon = other.reliableLatLon;
            archived = other.archived;
            found = other.found;
            own = other.own;
            disabled = other.disabled;
            favorite = other.favorite;
            onWatchlist = other.onWatchlist;
            logOffline = other.logOffline;
            finalDefined = other.finalDefined;
        }

        /*
         * No gathering for boolean members if a cache is not-detailed
         * - found
         * - own
         * - disabled
         * - favorite
         * - onWatchlist
         * - logOffline
         */
        if (visitedDate == 0) {
            visitedDate = other.visitedDate;
        }
        if (listId == StoredList.TEMPORARY_LIST_ID) {
            listId = other.listId;
        }
        if (StringUtils.isBlank(geocode)) {
            geocode = other.geocode;
        }
        if (StringUtils.isBlank(cacheId)) {
            cacheId = other.cacheId;
        }
        if (StringUtils.isBlank(guid)) {
            guid = other.guid;
        }
        if (null == cacheType || CacheType.UNKNOWN == cacheType) {
            cacheType = other.cacheType;
        }
        if (StringUtils.isBlank(name)) {
            name = other.name;
        }
        if (StringUtils.isBlank(nameSp)) {
            nameSp = other.nameSp;
        }
        if (StringUtils.isBlank(owner)) {
            owner = other.owner;
        }
        if (StringUtils.isBlank(ownerReal)) {
            ownerReal = other.ownerReal;
        }
        if (hidden == null) {
            hidden = other.hidden;
        }
        if (StringUtils.isBlank(hint)) {
            hint = other.hint;
        }
        if (size == null || CacheSize.UNKNOWN == size) {
            size = other.size;
        }
        if (difficulty == 0) {
            difficulty = other.difficulty;
        }
        if (terrain == 0) {
            terrain = other.terrain;
        }
        if (direction == null) {
            direction = other.direction;
        }
        if (distance == null) {
            distance = other.distance;
        }
        if (StringUtils.isBlank(latlon)) {
            latlon = other.latlon;
        }
        if (StringUtils.isBlank(location)) {
            location = other.location;
        }
        if (coords == null) {
            coords = other.coords;
        }
        if (zoomlevel < other.zoomlevel) {
            zoomlevel = other.zoomlevel;
            coords = other.coords;
        }

        if (elevation == null) {
            elevation = other.elevation;
        }
        if (personalNote == null) { // don't use StringUtils.isBlank here. Otherwise we cannot recognize a note which was deleted on GC
            personalNote = other.personalNote;
        }
        if (StringUtils.isBlank(shortdesc)) {
            shortdesc = other.shortdesc;
        }
        if (StringUtils.isBlank(description)) {
            description = other.description;
        }
        if (favoritePoints == 0) {
            favoritePoints = other.favoritePoints;
        }
        if (rating == 0) {
            rating = other.rating;
        }
        if (votes == 0) {
            votes = other.votes;
        }
        if (myVote == 0) {
            myVote = other.myVote;
        }
        if (attributes == null) {
            attributes = other.attributes;
        }
        if (waypoints == null) {
            waypoints = other.waypoints;
        }
        else {
            cgWaypoint.mergeWayPoints(waypoints, other.getWaypoints(), waypoints == null || waypoints.isEmpty());
        }
        if (spoilers == null) {
            spoilers = other.spoilers;
        }
        if (inventory == null) {
            // If inventoryItems is 0, it can mean both
            // "don't know" or "0 items". Since we cannot distinguish
            // them here, only populate inventoryItems from
            // old data when we have to do it for inventory.
            inventory = other.inventory;
            inventoryItems = other.inventoryItems;
        }
        if (CollectionUtils.isEmpty(logs)) { // keep last known logs if none
            logs = other.logs;
        }
        if (logCounts.size() == 0) {
            logCounts = other.logCounts;
        }
        if (!userModifiedCoords) {
            userModifiedCoords = other.userModifiedCoords;
        }
        if (!reliableLatLon) {
            reliableLatLon = other.reliableLatLon;
        }
        if (zoomlevel == -1) {
            zoomlevel = other.zoomlevel;
        }

        boolean isEqual = isEqualTo(other);

        if (!isEqual) {
            notifyChange();
        }

        return isEqual;
    }

    /**
     * Compare two caches quickly. For map and list fields only the references are compared !
     *
     * @param other
     * @return true if both caches have the same content
     */
    private boolean isEqualTo(final cgCache other) {
        return detailed == other.detailed &&
                StringUtils.equalsIgnoreCase(geocode, other.geocode) &&
                StringUtils.equalsIgnoreCase(name, other.name) &&
                cacheType == other.cacheType &&
                size == other.size &&
                found == other.found &&
                own == other.own &&
                premiumMembersOnly == other.premiumMembersOnly &&
                difficulty == other.difficulty &&
                terrain == other.terrain &&
                (coords != null ? coords.isEqualTo(other.coords) : null == other.coords) &&
                reliableLatLon == other.reliableLatLon &&
                disabled == other.disabled &&
                archived == other.archived &&
                listId == other.listId &&
                StringUtils.equalsIgnoreCase(owner, other.owner) &&
                StringUtils.equalsIgnoreCase(ownerReal, other.ownerReal) &&
                StringUtils.equalsIgnoreCase(description, other.description) &&
                StringUtils.equalsIgnoreCase(personalNote, other.personalNote) &&
                StringUtils.equalsIgnoreCase(shortdesc, other.shortdesc) &&
                StringUtils.equalsIgnoreCase(latlon, other.latlon) &&
                StringUtils.equalsIgnoreCase(location, other.location) &&
                favorite == other.favorite &&
                favoritePoints == other.favoritePoints &&
                onWatchlist == other.onWatchlist &&
                (hidden != null ? hidden.equals(other.hidden) : null == other.hidden) &&
                StringUtils.equalsIgnoreCase(guid, other.guid) &&
                StringUtils.equalsIgnoreCase(hint, other.hint) &&
                StringUtils.equalsIgnoreCase(cacheId, other.cacheId) &&
                (direction != null ? direction.equals(other.direction) : null == other.direction) &&
                (distance != null ? distance.equals(other.distance) : null == other.distance) &&
                (elevation != null ? elevation.equals(other.elevation) : null == other.elevation) &&
                nameSp == other.nameSp &&
                rating == other.rating &&
                votes == other.votes &&
                myVote == other.myVote &&
                inventoryItems == other.inventoryItems &&
                attributes == other.attributes &&
                waypoints == other.waypoints &&
                spoilers == other.spoilers &&
                logs == other.logs &&
                inventory == other.inventory &&
                logCounts == other.logCounts &&
                logOffline == other.logOffline &&
                finalDefined == other.finalDefined;
    }

    public boolean hasTrackables() {
        return inventoryItems > 0;
    }

    public boolean canBeAddedToCalendar() {
        // is event type?
        if (!isEventCache()) {
            return false;
        }
        // has event date set?
        if (hidden == null) {
            return false;
        }
        // is not in the past?
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (hidden.compareTo(cal.getTime()) < 0) {
            return false;
        }
        return true;
    }

    /**
     * checks if a page contains the guid of a cache
     *
     * @param cache
     *            the cache to look for
     * @param page
     *            the page to search in
     *
     * @return true: page contains guid of cache, false: otherwise
     */
    boolean isGuidContainedInPage(final String page) {
        if (StringUtils.isBlank(page)) {
            return false;
        }
        // check if the guid of the cache is anywhere in the page
        if (StringUtils.isBlank(guid)) {
            return false;
        }
        Pattern patternOk = Pattern.compile(guid, Pattern.CASE_INSENSITIVE);
        Matcher matcherOk = patternOk.matcher(page);
        if (matcherOk.find()) {
            Log.i(Settings.tag, "cgCache.isGuidContainedInPage: guid '" + guid + "' found");
            return true;
        } else {
            Log.i(Settings.tag, "cgCache.isGuidContainedInPage: guid '" + guid + "' not found");
            return false;
        }
    }

    public boolean isEventCache() {
        return CacheType.EVENT == cacheType || CacheType.MEGA_EVENT == cacheType
                || CacheType.CITO == cacheType || CacheType.LOSTANDFOUND == cacheType;
    }

    public boolean logVisit(IAbstractActivity fromActivity) {
        if (StringUtils.isBlank(cacheId)) {
            fromActivity.showToast(((Activity) fromActivity).getResources().getString(R.string.err_cannot_log_visit));
            return true;
        }
        Intent logVisitIntent = new Intent((Activity) fromActivity, VisitCacheActivity.class);
        logVisitIntent.putExtra(VisitCacheActivity.EXTRAS_ID, cacheId);
        logVisitIntent.putExtra(VisitCacheActivity.EXTRAS_GEOCODE, geocode.toUpperCase());
        logVisitIntent.putExtra(VisitCacheActivity.EXTRAS_FOUND, found);

        ((Activity) fromActivity).startActivity(logVisitIntent);

        return true;
    }

    public boolean logOffline(final IAbstractActivity fromActivity, final LogType logType) {
        String log = "";
        if (StringUtils.isNotBlank(Settings.getSignature())
                && Settings.isAutoInsertSignature()) {
            log = LogTemplateProvider.applyTemplates(Settings.getSignature(), true);
        }
        logOffline(fromActivity, log, Calendar.getInstance(), logType);
        return true;
    }

    void logOffline(final IAbstractActivity fromActivity, final String log, Calendar date, final LogType logType) {
        if (logType == LogType.LOG_UNKNOWN) {
            return;
        }
        cgeoapplication app = (cgeoapplication) ((Activity) fromActivity).getApplication();
        final boolean status = app.saveLogOffline(geocode, date.getTime(), logType, log);

        Resources res = ((Activity) fromActivity).getResources();
        if (status) {
            fromActivity.showToast(res.getString(R.string.info_log_saved));
            app.saveVisitDate(geocode);
            logOffline = true;

            notifyChange();
        } else {
            fromActivity.showToast(res.getString(R.string.err_log_post_failed));
        }
    }

    public List<LogType> getPossibleLogTypes() {
        boolean isOwner = owner != null && owner.equalsIgnoreCase(Settings.getUsername());
        List<LogType> logTypes = new ArrayList<LogType>();
        if (isEventCache()) {
            logTypes.add(LogType.LOG_WILL_ATTEND);
            logTypes.add(LogType.LOG_NOTE);
            logTypes.add(LogType.LOG_ATTENDED);
            logTypes.add(LogType.LOG_NEEDS_ARCHIVE);
            if (isOwner) {
                logTypes.add(LogType.LOG_ANNOUNCEMENT);
            }
        } else if (CacheType.WEBCAM == cacheType) {
            logTypes.add(LogType.LOG_WEBCAM_PHOTO_TAKEN);
            logTypes.add(LogType.LOG_DIDNT_FIND_IT);
            logTypes.add(LogType.LOG_NOTE);
            logTypes.add(LogType.LOG_NEEDS_ARCHIVE);
            logTypes.add(LogType.LOG_NEEDS_MAINTENANCE);
        } else {
            logTypes.add(LogType.LOG_FOUND_IT);
            logTypes.add(LogType.LOG_DIDNT_FIND_IT);
            logTypes.add(LogType.LOG_NOTE);
            logTypes.add(LogType.LOG_NEEDS_ARCHIVE);
            logTypes.add(LogType.LOG_NEEDS_MAINTENANCE);
        }
        if (isOwner) {
            logTypes.add(LogType.LOG_OWNER_MAINTENANCE);
            logTypes.add(LogType.LOG_TEMP_DISABLE_LISTING);
            logTypes.add(LogType.LOG_ENABLE_LISTING);
            logTypes.add(LogType.LOG_ARCHIVE);
            logTypes.remove(LogType.LOG_UPDATE_COORDINATES);
        }
        return logTypes;
    }

    public void openInBrowser(Activity fromActivity) {
        fromActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getCacheUrl())));
    }

    private String getCacheUrl() {
        return getConnector().getCacheUrl(this);
    }

    private IConnector getConnector() {
        return ConnectorFactory.getConnector(this);
    }

    public boolean canOpenInBrowser() {
        return getCacheUrl() != null;
    }

    public boolean supportsRefresh() {
        return getConnector().supportsRefreshCache(this);
    }

    public boolean supportsWatchList() {
        return getConnector().supportsWatchList();
    }

    public boolean supportsLogging() {
        return getConnector().supportsLogging();
    }

    @Override
    public float getDifficulty() {
        return difficulty;
    }

    @Override
    public String getGeocode() {
        return geocode;
    }

    @Override
    public String getLatitude() {
        return coords != null ? coords.format(GeopointFormatter.Format.LAT_DECMINUTE) : null;
    }

    @Override
    public String getLongitude() {
        return coords != null ? coords.format(GeopointFormatter.Format.LON_DECMINUTE) : null;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public CacheSize getSize() {
        if (size == null) {
            return CacheSize.UNKNOWN;
        }
        return size;
    }

    @Override
    public float getTerrain() {
        return terrain;
    }

    @Override
    public boolean isArchived() {
        return archived;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public boolean isPremiumMembersOnly() {
        return premiumMembersOnly;
    }

    public void setPremiumMembersOnly(boolean members) {
        this.premiumMembersOnly = members;
    }

    @Override
    public boolean isOwn() {
        return own;
    }

    @Override
    public String getOwnerReal() {
        return ownerReal;
    }

    @Override
    public String getHint() {
        return hint;
    }

    @Override
    public String getDescription() {
        if (description == null) {
            description = StringUtils.defaultString(cgeoapplication.getInstance().getCacheDescription(geocode));
        }
        return description;
    }

    @Override
    public String getShortDescription() {
        return shortdesc;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCacheId() {
        if (StringUtils.isBlank(cacheId) && getConnector().equals(GCConnector.getInstance())) {
            return String.valueOf(GCBase.gccodeToGCId(geocode));
        }

        return cacheId;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getPersonalNote() {
        // non premium members have no personal notes, premium members have an empty string by default.
        // map both to null, so other code doesn't need to differentiate
        if (StringUtils.isBlank(personalNote)) {
            return null;
        }
        return personalNote;
    }

    public boolean supportsUserActions() {
        return getConnector().supportsUserActions();
    }

    public boolean supportsCachesAround() {
        return getConnector().supportsCachesAround();
    }

    public void shareCache(Activity fromActivity, Resources res) {
        if (geocode == null) {
            return;
        }

        StringBuilder subject = new StringBuilder("Geocache ");
        subject.append(geocode.toUpperCase());
        if (StringUtils.isNotBlank(name)) {
            subject.append(" - ").append(name);
        }

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject.toString());
        intent.putExtra(Intent.EXTRA_TEXT, getUrl());

        fromActivity.startActivity(Intent.createChooser(intent, res.getText(R.string.action_bar_share_title)));
    }

    public String getUrl() {
        return getConnector().getCacheUrl(this);
    }

    public boolean supportsGCVote() {
        return StringUtils.startsWithIgnoreCase(geocode, "GC");
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public boolean isFound() {
        return found;
    }

    @Override
    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favourite) {
        this.favorite = favourite;
    }


    @Override
    public boolean isWatchlist() {
        return onWatchlist;
    }

    @Override
    public Date getHiddenDate() {
        return hidden;
    }

    @Override
    public List<String> getAttributes() {
        if (attributes == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(attributes);
    }

    @Override
    public List<cgTrackable> getInventory() {
        return inventory;
    }

    @Override
    public ArrayList<cgImage> getSpoilers() {
        return spoilers;
    }

    @Override
    public Map<LogType, Integer> getLogCounts() {
        return logCounts;
    }

    @Override
    public int getFavoritePoints() {
        return favoritePoints;
    }

    @Override
    public String getNameForSorting() {
        if (null == nameForSorting) {
            final Matcher matcher = NUMBER_PATTERN.matcher(name);
            if (matcher.find()) {
                nameForSorting = name.replace(matcher.group(), StringUtils.leftPad(matcher.group(), 6, '0'));
            }
            else {
                nameForSorting = name;
            }
        }
        return nameForSorting;
    }

    public boolean isVirtual() {
        return CacheType.VIRTUAL == cacheType || CacheType.WEBCAM == cacheType
                || CacheType.EARTH == cacheType;
    }

    public boolean showSize() {
        return !((isEventCache() || isVirtual()) && size == CacheSize.NOT_CHOSEN);
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getDetailedUpdate() {
        return detailedUpdate;
    }

    public void setDetailedUpdate(long detailedUpdate) {
        this.detailedUpdate = detailedUpdate;
    }

    public long getVisitedDate() {
        return visitedDate;
    }

    public void setVisitedDate(long visitedDate) {
        this.visitedDate = visitedDate;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public boolean isDetailed() {
        return detailed;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }

    public Spannable getNameSp() {
        return nameSp;
    }

    public void setNameSp(Spannable nameSp) {
        this.nameSp = nameSp;
    }

    public void setHidden(final Date hidden) {
        if (hidden == null) {
            this.hidden = null;
        }
        else {
            this.hidden = new Date(hidden.getTime()); // avoid storing the external reference in this object
        }
    }

    public Float getDirection() {
        return direction;
    }

    public void setDirection(Float direction) {
        this.direction = direction;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public String getLatlon() {
        return latlon;
    }

    public void setLatlon(String latlon) {
        this.latlon = latlon;
    }

    public Geopoint getCoords() {
        return coords;
    }

    public void setCoords(Geopoint coords) {
        this.coords = coords;
    }

    /**
     * @return true if the coords are from the cache details page and the user has been logged in
     */
    public boolean isReliableLatLon() {
        return getConnector().isReliableLatLon(reliableLatLon);
    }

    public void setReliableLatLon(boolean reliableLatLon) {
        this.reliableLatLon = reliableLatLon;
    }

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public void setFavoritePoints(int favoriteCnt) {
        this.favoritePoints = favoriteCnt;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public float getMyVote() {
        return myVote;
    }

    public void setMyVote(float myVote) {
        this.myVote = myVote;
    }

    public int getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(int inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public boolean isOnWatchlist() {
        return onWatchlist;
    }

    public void setOnWatchlist(boolean onWatchlist) {
        this.onWatchlist = onWatchlist;
    }

    /**
     * return an immutable list of waypoints.
     *
     * @return always non <code>null</code>
     */
    public List<cgWaypoint> getWaypoints() {
        if (waypoints == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(waypoints);
    }

    /**
     * @param waypoints
     *            List of waypoints to set for cache
     * @param saveToDatabase
     *            Indicates whether to add the waypoints to the database. Should be false if
     *            called while loading or building a cache
     * @return <code>true</code> if waypoints successfully added to waypoint database
     */
    public boolean setWaypoints(List<cgWaypoint> waypoints, boolean saveToDatabase) {
        this.waypoints = waypoints;
        finalDefined = false;
        if (waypoints != null) {
            for (cgWaypoint waypoint : waypoints) {
                waypoint.setGeocode(geocode);
                if (waypoint.isFinalWithCoords()) {
                    finalDefined = true;
                }
            }
        }

        if (saveToDatabase) {
            return cgeoapplication.getInstance().saveWaypoints(geocode, waypoints, false);
        }

        return false;
    }

    public List<cgLog> getLogs() {
        return getLogs(true);
    }

    /**
     * @param allLogs
     *            true for all logs, false for friend logs only
     * @return the logs with all entries or just the entries of the friends, never <code>null</code>
     */
    public List<cgLog> getLogs(boolean allLogs) {
        if (logs == null) {
            return Collections.emptyList();
        }
        if (allLogs) {
            return logs;
        }
        ArrayList<cgLog> friendLogs = new ArrayList<cgLog>();
        for (cgLog log : logs) {
            if (log.friend) {
                friendLogs.add(log);
            }
        }
        return friendLogs;
    }

    /**
     * @param logs
     *            the log entries
     */
    public void setLogs(List<cgLog> logs) {
        this.logs = logs;
    }

    public boolean isLogOffline() {
        return logOffline;
    }

    public void setLogOffline(boolean logOffline) {
        this.logOffline = logOffline;
    }

    public boolean isStatusChecked() {
        return statusChecked;
    }

    public void setStatusChecked(boolean statusChecked) {
        this.statusChecked = statusChecked;
    }

    public boolean isStatusCheckedView() {
        return statusCheckedView;
    }

    public void setStatusCheckedView(boolean statusCheckedView) {
        this.statusCheckedView = statusCheckedView;
    }

    public String getDirectionImg() {
        return directionImg;
    }

    public void setDirectionImg(String directionImg) {
        this.directionImg = directionImg;
    }

    public void setGeocode(String geocode) {
        this.geocode = geocode;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOwnerReal(String ownerReal) {
        this.ownerReal = ownerReal;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setSize(CacheSize size) {
        if (size == null) {
            this.size = CacheSize.UNKNOWN;
        }
        else {
            this.size = size;
        }
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public void setTerrain(float terrain) {
        this.terrain = terrain;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPersonalNote(String personalNote) {
        this.personalNote = personalNote;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public void setSpoilers(ArrayList<cgImage> spoilers) {
        this.spoilers = spoilers;
    }

    public void setInventory(List<cgTrackable> inventory) {
        this.inventory = inventory;
    }

    public void setLogCounts(Map<LogType, Integer> logCounts) {
        this.logCounts = logCounts;
    }

    /*
     * (non-Javadoc)
     *
     * @see cgeo.geocaching.IBasicCache#getType()
     *
     * @returns Never null
     */
    @Override
    public CacheType getType() {
        return cacheType;
    }

    public void setType(CacheType cacheType) {
        if (cacheType == null || CacheType.ALL == cacheType) {
            throw new IllegalArgumentException("Illegal cache type");
        }
        this.cacheType = cacheType;
    }

    public boolean hasDifficulty() {
        return difficulty > 0f;
    }

    public boolean hasTerrain() {
        return terrain > 0f;
    }

    /**
     * @return the storageLocation
     */
    public EnumSet<StorageLocation> getStorageLocation() {
        return storageLocation;
    }

    /**
     * @param storageLocation
     *            the storageLocation to set
     */
    public void addStorageLocation(StorageLocation sl) {
        this.storageLocation.add(sl);
    }

    /**
     * @param waypoint
     *            Waypoint to add to the cache
     * @param saveToDatabase
     *            Indicates whether to add the waypoint to the database. Should be false if
     *            called while loading or building a cache
     * @return <code>true</code> if waypoint successfully added to waypoint database
     */
    public boolean addWaypoint(final cgWaypoint waypoint, boolean saveToDatabase) {
        if (null == waypoints) {
            waypoints = new ArrayList<cgWaypoint>();
        }

        waypoint.setGeocode(geocode);

        if (waypoint.getId() <= 0) { // this is a new waypoint
            waypoints.add(waypoint);
            if (waypoint.isFinalWithCoords()) {
                finalDefined = true;
            }
        } else { // this is a waypoint being edited
            final int index = getWaypointIndex(waypoint);
            if (index >= 0) {
                waypoints.remove(index);
            }
            waypoints.add(waypoint);
            // when waypoint was edited, finalDefined may have changed
            resetFinalDefined();
        }

        if (saveToDatabase) {
            return cgeoapplication.getInstance().saveOwnWaypoint(waypoint.getId(), geocode, waypoint);
        }

        return false;
    }

    public boolean hasWaypoints() {
        return CollectionUtils.isNotEmpty(waypoints);
    }

    public boolean hasFinalDefined() {
        return finalDefined;
    }

    // Only for loading
    public void setFinalDefined(boolean finalDefined) {
        this.finalDefined = finalDefined;
    }

    /**
     * Reset <code>finalDefined</code> based on current list of stored waypoints
     */
    private void resetFinalDefined() {
        finalDefined = false;
        for (cgWaypoint wp : waypoints) {
            if (wp.isFinalWithCoords()) {
                finalDefined = true;
                break;
            }
        }
    }

    public boolean hasUserModifiedCoords() {
        return userModifiedCoords;
    }

    public void setUserModifiedCoords(boolean coordsChanged) {
        this.userModifiedCoords = coordsChanged;
    }

    /**
     * @param index
     * @return <code>true</code>, if the waypoint was duplicated
     */
    public boolean duplicateWaypoint(int index) {
        if (!isValidWaypointIndex(index)) {
            return false;
        }
        final cgWaypoint copy = new cgWaypoint(waypoints.get(index));
        copy.setUserDefined();
        copy.setName(cgeoapplication.getInstance().getString(R.string.waypoint_copy_of) + " " + copy.getName());
        waypoints.add(index + 1, copy);
        return cgeoapplication.getInstance().saveOwnWaypoint(-1, geocode, copy);
    }

    private boolean isValidWaypointIndex(int index) {
        if (!hasWaypoints()) {
            return false;
        }
        if (index < 0 || index >= waypoints.size()) {
            return false;
        }
        return true;
    }

    /**
     * delete a user defined waypoint
     *
     * @param index
     *            of the waypoint in cache's waypoint list
     * @return <code>true</code>, if the waypoint was deleted
     */
    public boolean deleteWaypoint(int index) {
        if (!isValidWaypointIndex(index)) {
            return false;
        }
        final cgWaypoint waypoint = waypoints.get(index);
        if (waypoint.isUserDefined()) {
            waypoints.remove(index);
            cgeoapplication.getInstance().deleteWaypoint(waypoint.getId());
            cgeoapplication.getInstance().removeCache(geocode, EnumSet.of(RemoveFlag.REMOVE_CACHE));
            // Check status if Final is defined
            if (waypoint.isFinalWithCoords()) {
                resetFinalDefined();
            }
            return true;
        }
        return false;
    }

    /**
     * delete a user defined waypoint
     *
     * @param waypoint
     *            to be removed from cache
     * @return <code>true</code>, if the waypoint was deleted
     */
    public boolean deleteWaypoint(cgWaypoint waypoint) {
        if (waypoint.getId() <= 0) {
            return false;
        }

        final int index = getWaypointIndex(waypoint);
        if (index >= 0) {
            return deleteWaypoint(index);
        }

        return false;
    }

    /**
     * Find index of given <code>waypoint</code> in cache's <code>waypoints</code> list
     *
     * @param waypoint
     *            to find index for
     * @return index in <code>waypoints</code> if found, else -1
     */
    private int getWaypointIndex(cgWaypoint waypoint) {
        int index = 0;

        for (cgWaypoint wp : waypoints) {
            if (wp.getId() == waypoint.getId()) {
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * @param index
     * @return waypoint or <code>null</code>
     */
    public cgWaypoint getWaypoint(int index) {
        if (!isValidWaypointIndex(index)) {
            return null;
        }
        return waypoints.get(index);
    }

    /**
     * @param index
     * @return waypoint or <code>null</code>
     */
    public cgWaypoint getWaypointById(int id) {
        for (cgWaypoint waypoint : waypoints) {
            if (waypoint.getId() == id) {
                return waypoint;
            }
        }
        return null;
    }

    public void parseWaypointsFromNote() {
        try {
            if (StringUtils.isBlank(getPersonalNote())) {
                return;
            }
            final Pattern coordPattern = Pattern.compile("\\b[nNsS]{1}\\s*\\d"); // begin of coordinates
            int count = 1;
            String note = getPersonalNote();
            Matcher matcher = coordPattern.matcher(note);
            while (matcher.find()) {
                try {
                    final Geopoint point = GeopointParser.parse(note.substring(matcher.start()));
                    // coords must have non zero latitude and longitude and at least one part shall have fractional degrees
                    if (point != null && point.getLatitudeE6() != 0 && point.getLongitudeE6() != 0 && ((point.getLatitudeE6() % 1000) != 0 || (point.getLongitudeE6() % 1000) != 0)) {
                        final String name = cgeoapplication.getInstance().getString(R.string.cache_personal_note) + " " + count;
                        final cgWaypoint waypoint = new cgWaypoint(name, WaypointType.WAYPOINT, false);
                        waypoint.setCoords(point);
                        addWaypoint(waypoint, false);
                        count++;
                    }
                } catch (GeopointParser.ParseException e) {
                    // ignore
                }

                note = note.substring(matcher.start() + 1);
                matcher = coordPattern.matcher(note);
            }
        } catch (Exception e) {
            Log.e(Settings.tag, "cgCache.parseWaypointsFromNote: " + e.toString());
        }
    }

    public void addAttribute(final String attribute) {
        if (attributes == null) {
            attributes = new ArrayList<String>();
        }
        attributes.add(attribute);
    }

    public boolean hasAttributes() {
        return attributes != null && attributes.size() > 0;
    }

    public void prependLog(final cgLog log) {
        if (logs == null) {
            logs = new ArrayList<cgLog>();
        }
        logs.add(0, log);
    }

    public void appendLog(final cgLog log) {
        if (logs == null) {
            logs = new ArrayList<cgLog>();
        }
        logs.add(log);
    }

    /*
     * For working in the debugger
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.geocode + " " + this.name;
    }

    @Override
    public int hashCode() {
        return geocode.hashCode() * name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof cgCache)) {
            return false;
        }
        // just compare the geocode even if that is not what "equals" normally does
        return StringUtils.isNotBlank(geocode) ? geocode.equals(((cgCache) obj).geocode) : false;
    }

    public void store(Activity activity, CancellableHandler handler) {
        final int listId = Math.max(getListId(), StoredList.STANDARD_LIST_ID);
        cgBase.storeCache(activity, this, null, listId, false, handler);
    }

    public int getZoomlevel() {
        return this.zoomlevel;
    }

    public void setZoomlevel(int zoomlevel) {
        this.zoomlevel = zoomlevel;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public WaypointType getWaypointType() {
        return null;
    }

    @Override
    public String getCoordType() {
        return "cache";
    }
}
