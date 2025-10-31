package cz.uhk.loadtesterapp.repository;


import cz.uhk.loadtesterapp.model.entity.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface TestRepository extends JpaRepository<TestRun, Long>, JpaSpecificationExecutor<TestRun> {

}

