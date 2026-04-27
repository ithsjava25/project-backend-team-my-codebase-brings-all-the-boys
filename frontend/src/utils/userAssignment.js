/**
 * Consolidates all student-uploaded files (direct and via submissions).
 * 
 * @param {Object} ua - UserAssignment object
 * @returns {Array} Array of deduplicated file objects
 */
export function mergeStudentFiles(ua) {
    if (!ua) return [];
    
    const directFiles = ua.files || [];
    const submissionFiles = (ua.submissions || [])
        .flatMap(s => s.files || []);
    
    // Use a Map to ensure uniqueness by ID
    const fileMap = new Map();
    [...directFiles, ...submissionFiles].forEach(f => fileMap.set(f.id, f));
    
    return Array.from(fileMap.values());
}

/**
 * Deterministically get the latest submission by submittedAt date.
 * 
 * @param {Object} ua - UserAssignment object
 * @returns {Object|null} The latest submission or null
 */
export function getLatestSubmission(ua) {
    if (!ua || !ua.submissions || ua.submissions.length === 0) return null;
    
    return [...ua.submissions].sort((a, b) => {
        const dateA = a.submittedAt ? new Date(a.submittedAt) : new Date(0);
        const dateB = b.submittedAt ? new Date(b.submittedAt) : new Date(0);
        return dateB - dateA;
    })[0];
}
