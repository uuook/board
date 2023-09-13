package com.support.board.mapper;

import com.support.board.domain.Board;
import com.support.board.dto.RequestBoardDto;
import com.support.board.dto.ResponseBoardDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BoardMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBoardFromRequest(RequestBoardDto requestBoardDto, @MappingTarget Board board);
}
