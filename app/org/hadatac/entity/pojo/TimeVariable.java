package org.hadatac.entity.pojo;

public class TimeVariable {

    private StudyObject obj;
    private Entity ent;
    private String role;
    private Attribute attr;
    private Entity inRelationTo;
    private Unit unit;

    public TimeVariable(StudyObject obj, AlignmentEntityRole entRole, AttributeInRelationTo attrInRel) {
        this(obj, entRole, attrInRel, null);
    }

    public TimeVariable(StudyObject obj, AlignmentEntityRole entRole, AttributeInRelationTo attrInRel, Unit unit) {
        this.obj = obj;
        this.ent = entRole.getEntity();
        this.role = entRole.getRole();
        this.attr = attrInRel.getAttribute();
        this.inRelationTo = attrInRel.getInRelationTo();
        this.unit = unit;
    }

    public String getKey() {
        return getObject() + getRole() + getEntityStr() + getAttributeStr() + getInRelationToStr() + getUnitStr();
    }

    public String getObject() {
        return obj.getOriginalId();
    }

    public Entity getEntity() {
        return ent;
    }

    public String getEntityStr() {
        if (ent != null && ent.getUri() != null && !ent.getUri().equals("")) {
            return ent.getUri();
        }
        return "";
    }

    public String getRole() {
        return role;
    }

    public Attribute getAttribute() {
        return attr;
    }

    public String getAttributeStr() {
        if (attr != null && attr.getUri() != null && !attr.getUri().equals("")) {
            return attr.getUri();
        }
        return "";
    }

    public Entity getInRelationTo() {
        return inRelationTo;
    }

    public String getInRelationToStr() {
        if (inRelationTo != null && inRelationTo.getUri() != null && !inRelationTo.getUri().equals("")) {
            return inRelationTo.getUri();
        }
        return "";
    }

    public Unit getUnit() {
        return unit;
    }

    public String getUnitStr() {
        if (unit != null && unit.getUri() != null && !unit.getUri().equals("")) {
            return unit.getUri();
        }
        return "";
    }

    public static String upperCase(String orig) {
        String[] words = orig.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < words.length; i++) {
            sb.append(Character.toUpperCase(words[i].charAt(0)))
                    .append(words[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public String prep(String orig) {
        String aux = upperCase(orig);
        return aux.replaceAll(" ","-").replaceAll("[()]","");
    }

    public String toString() {
        //System.out.println("[" + attr.getLabel() + "]");
        String str = "";
        if (obj.getOriginalId() != null && !obj.getOriginalId().equals("")) {
            str += prep(obj.getOriginalId()) + "-";
        }
        if (role != null && !role.equals("")) {
            str += prep(role) + "-";
        }
        str += prep(ent.getLabel()) + "-" + prep(attr.getLabel());
        if (inRelationTo != null && !inRelationTo.getLabel().equals("")) {
            str += "-" + prep(inRelationTo.getLabel());
        }
        if (unit != null) {
            str += "-" + prep(unit.getLabel());
        }
        return str;
    }

}
