package com.trifork;

import com.trifork.vo.DosageDrug;
import com.trifork.vo.DosageUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
public class DrugDAO {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Collection<DosageDrug> getAllDrugs(String tablePostFix) {
        return jdbcTemplate.query("SELECT *, du.* FROM DosageDrug" + tablePostFix + " as dd JOIN DosageUnit" + tablePostFix +
                " du ON du.code = dd.dosageUnitCode", drugRowMapper);
    }

    public Collection<DosageUnit> getAllUnits(String table) {
        return jdbcTemplate.query("SELECT * FROM " + table, unitRowMapper);
    }

    public String getShortSuggestionText(Long drugId, String tablePostFix) {
        try {
            Long structureCode = jdbcTemplate.queryForObject("SELECT dosageStructureCode FROM DrugDosageStructureOldRelation" + tablePostFix +
                    " WHERE drugId=? LIMIT 1", Long.class, drugId);
            return jdbcTemplate.queryForObject("SELECT shortTranslation FROM DosageStructure" + tablePostFix +
                    " WHERE code=?", String.class, structureCode);
        } catch (EmptyResultDataAccessException ignored) {
        }
        return null;
    }

    private static RowMapper<DosageUnit> unitRowMapper = new RowMapper<DosageUnit>() {
        @Override
        public DosageUnit mapRow(ResultSet rs, int rowNum) throws SQLException {
            DosageUnit du = new DosageUnit();
            du.setCode(rs.getLong("code"));
            du.setSingular(rs.getString("textSingular"));
            du.setPlural(rs.getString("textPlural"));
            return du;
        }
    };

    private static RowMapper<DosageDrug> drugRowMapper = new RowMapper<DosageDrug>() {
        @Override
        public DosageDrug mapRow(ResultSet rs, int rowNum) throws SQLException {
            DosageDrug dd = new DosageDrug();
            dd.setDrugId(rs.getLong("drugId"));
            dd.setDosageUnitCode(rs.getLong("dosageUnitCode"));
            dd.setDrugName(rs.getString("drugName"));
            dd.setUnit(unitRowMapper.mapRow(rs, rowNum));
            return dd;
        }
    };

}
