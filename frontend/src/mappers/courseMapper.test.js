import {describe, it, expect} from 'vitest';
import {
    mapToCourseDetailFormat,
    mapToSidebarFormat,
    mapToCardFormat,
    mapToTableRowFormat,
} from './courseMapper';

describe('mapToCourseDetailFormat', () => {
    it('maps course data correctly', () => {
        const apiData = {
            id: '123',
            name: 'Test',
            schoolClassName: 'TE23',
            leadTeacher: {username: 'anna', email: 'anna@school.com'},
            assistants: [],
            assignments: [],
            students: [],
        };

        const result = mapToCourseDetailFormat(apiData);

        expect(result).toEqual({
            id: '123',
            name: 'Test',
            schoolClassName: 'TE23',
            description: undefined,
            leadTeacher: {username: 'anna', email: 'anna@school.com'},
            assistants: [],
            assignments: [],
            students: [],
        });
    });

    it('returns null if no course sent in', () => {
        expect(mapToCourseDetailFormat(null)).toBeNull();
    });

    it('handles missing leadTeacher', () => {
        const result = mapToCourseDetailFormat({id: '1', name: 'Course'});
        expect(result.leadTeacher).toBeNull();
    });

    it('uses empty array as default for assistants and assignments', () => {
        const result = mapToCourseDetailFormat({id: '1', name: 'Course'});
        expect(result.assistants).toEqual([]);
        expect(result.assignments).toEqual([]);
    });
});

describe('mapToSidebarFormat', () => {
    it('maps a list of courses to sidebar-format', () => {
        const courses = [{id: '1', name: 'Math', schoolClassName: 'TE23'}];
        const result = mapToSidebarFormat(courses);

        expect(result[0].id).toBe('1');
        expect(result[0].name).toBe('Math');
        expect(result[0].url).toBe('/courses/1');
        expect(result[0].schoolClassName).toBe('TE23');
    });

    it('returns an empty array if input is not an array', () => {
        expect(mapToSidebarFormat(null)).toEqual([]);
        expect(mapToSidebarFormat(undefined)).toEqual([]);
    });
});

describe('mapToCardFormat', () => {
    it('maps courses to card format', () => {
        const courses = [{id: '1', name: 'Physics', schoolClassName: 'NA22'}];
        const result = mapToCardFormat(courses);

        expect(result[0]).toEqual({id: '1', name: 'Physics', class: 'NA22'});
    });

    it('returns an empty array if input is not an array', () => {
        expect(mapToCardFormat(null)).toEqual([]);
    });
});

describe('mapToTableRowFormat', () => {
    it('maps courses to table row format', () => {
        const courses = [{id: '1', name: 'Chemistry', schoolClassName: 'NA22'}];
        const result = mapToTableRowFormat(courses);

        expect(result[0]).toEqual({id: '1', name: 'Chemistry', schoolClass: 'NA22'});
    });

    it('returns empty array if input is not an array', () => {
        expect(mapToTableRowFormat(undefined)).toEqual([]);
    });
});