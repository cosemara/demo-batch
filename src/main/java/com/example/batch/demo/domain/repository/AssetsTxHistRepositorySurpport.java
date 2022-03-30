package com.example.batch.demo.domain.repository;

import com.example.batch.demo.domain.entity.AssetsTxHistory;
import com.example.batch.demo.domain.type.AssetsTxType;
import com.example.batch.demo.domain.type.AssetsType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.batch.demo.domain.entity.QAssetsTxHistory.assetsTxHistory;
import static com.example.batch.demo.domain.entity.QUser.user;

@Repository
public class AssetsTxHistRepositorySurpport extends QuerydslRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public AssetsTxHistRepositorySurpport(JPAQueryFactory jpaQueryFactory) {
        super(AssetsTxHistory.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public PageImpl<AssetsTxHistory> findAllByUserId(Long userId, Pageable pageable) {
        JPQLQuery<AssetsTxHistory> query = jpaQueryFactory
                .selectFrom(assetsTxHistory)
                .join(assetsTxHistory.user, user).fetchJoin()
                .where(assetsTxHistory.user.id.eq(userId));

        Long totalCount = query.fetchCount();
        List<AssetsTxHistory> list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(list, pageable, totalCount);
    }

    public PageImpl<AssetsTxHistory> findAllByUserIdAndAssetsTypeAndAssetsTxType(Long userId, AssetsType assetsType, AssetsTxType assetsTxType, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        JPQLQuery<AssetsTxHistory> query = jpaQueryFactory
                .selectFrom(assetsTxHistory)
                .join(assetsTxHistory.user, user).fetchJoin()
                .where(assetsTxHistory.user.id.eq(userId),
                        eqAssetsType(assetsType),
                        eqAssetsTxType(assetsTxType),
                        assetsTxHistory.txDate.gt(startTime),
                        assetsTxHistory.txDate.loe(endTime)
                );

        Long totalCount = query.fetchCount();
        List<AssetsTxHistory> list = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(list, pageable, totalCount);
    }

    private BooleanExpression eqAssetsType(AssetsType assetsType) {
        if (assetsType == null) {
            return null;
        }
        return assetsTxHistory.assetsType.eq(assetsType);
    }

    private BooleanExpression eqAssetsTxType(AssetsTxType assetsTxType) {
        if (assetsTxType == null) {
            return null;
        }
        return assetsTxHistory.assetsTxType.eq(assetsTxType);
    }
}
