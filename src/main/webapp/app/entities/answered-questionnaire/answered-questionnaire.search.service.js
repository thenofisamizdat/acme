(function() {
    'use strict';

    angular
        .module('acmeApp')
        .factory('AnsweredQuestionnaireSearch', AnsweredQuestionnaireSearch);

    AnsweredQuestionnaireSearch.$inject = ['$resource'];

    function AnsweredQuestionnaireSearch($resource) {
        var resourceUrl =  'api/_search/answered-questionnaires/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
