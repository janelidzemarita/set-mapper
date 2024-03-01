package com.epam.rd.autocode;

import com.epam.rd.autocode.domain.Employee;
import com.epam.rd.autocode.domain.FullName;
import com.epam.rd.autocode.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SetMapperFactory {
    public SetMapper<Set<Employee>> employeesSetMapper() {
        class SetMapperImpl implements SetMapper<Set<Employee>> {
            private static final String HIREDATE = "hiredate";

            @Override
            public Set<Employee> mapSet(ResultSet resultSet) {
                Set<Employee> set = new HashSet<>();
                try {
                    while (resultSet.next()) {
                        set.add(getEmployee(resultSet));
                        resultSet.absolute(resultSet.getRow());
                    }
                } catch (SQLException e) {
                    throw new IllegalArgumentException(e);
                }
                return set;
            }

            private Employee getEmployee(ResultSet resultSet) throws SQLException {
                return new Employee(
                        getId(resultSet),
                        getFullName(resultSet),
                        getPosition(resultSet),
                        getHireDate(resultSet),
                        getSalary(resultSet),
                        getManager(resultSet.getString("manager"), resultSet));
            }

            private Employee getManager(String managerID, ResultSet resultSet) {
                if (managerID != null) {
                    try {
                        return findManager(managerID, resultSet);
                    } catch (SQLException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
                return null;
            }

            private Employee findManager(String managerID, ResultSet resultSet) throws SQLException {
                resultSet.beforeFirst();
                while (resultSet.next()) {
                    if (managerID.equals(resultSet.getString("ID"))) {
                        return getEmployee(resultSet);
                    }
                }
                return null;
            }

            private BigInteger getId(ResultSet resultSet) throws SQLException {
                return new BigInteger(resultSet.getString("ID"));
            }

            private FullName getFullName(ResultSet resultSet) throws SQLException {
                return new FullName((resultSet.getString("firstName")), (resultSet.getString("lastName")),
                        (resultSet.getString("middleName")));
            }

            private Position getPosition(ResultSet resultSet) throws SQLException {
                return Position.valueOf(resultSet.getString("position"));
            }

            private LocalDate getHireDate(ResultSet resultSet) throws SQLException {
                return resultSet.getDate(HIREDATE).toLocalDate();
            }

            private BigDecimal getSalary(ResultSet resultSet) throws SQLException {
                return new BigDecimal(resultSet.getInt("salary"));
            }
        }
        return new SetMapperImpl();
    }
}
