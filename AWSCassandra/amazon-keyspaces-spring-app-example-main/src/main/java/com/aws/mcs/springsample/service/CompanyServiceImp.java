package com.aws.mcs.springsample.service;

import com.aws.mcs.springsample.model.Company;
import com.aws.mcs.springsample.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.*;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.springframework.data.cassandra.core.query.Criteria.where;

@Service
public class CompanyServiceImp implements CompanyService {

    private final CompanyRepository repository;
    private final CassandraOperations template;

    @Autowired
    public CompanyServiceImp(CompanyRepository repository, CassandraOperations template) {
        this.repository = repository;
        this.template = template;
    }

    @Override
    public List<Company> getAll() {
        return template.select(Query.empty(), Company.class);
    }

    @Override
    public Company getCompanyById(String id) {
        return template.selectOne(Query.query(where("companyId").is(id)), Company.class);
    }

    @Override
    public Company newCompany(Company newCompany) {
        Company comp = new Company(UUID.randomUUID().toString(), newCompany.getCompanyName(), newCompany.getUniqueBusinessIdentifier());

        EntityWriteResult<Company> company = template.insert(comp, repository.insertOptions());

        return company.getEntity();
    }

    @Override
    public Company updateCompany(String id, Company newCompany) {

        Company existCompany = template.selectOne(Query.query(where("companyId").is(id)), Company.class);

        if (existCompany != null) {
            existCompany.setCompanyName(newCompany.getCompanyName());
            existCompany.setUniqueBusinessIdentifier(newCompany.getUniqueBusinessIdentifier());

            EntityWriteResult<Company> updateCompany = template.update(existCompany, repository.updateOptions());
            existCompany = updateCompany.getEntity();
        }

        return existCompany;
    }

    @Override
    public String deteleCompany(String id) {
        Company company = template.selectOne(Query.query(where("companyId").is(id)), Company.class);

        if (company != null) {
            template.delete(company, repository.deleteOptions());
            return "Company deleted success";
        }

        return "Not company found";
    }
}
