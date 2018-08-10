(function() {
    'use strict';

    angular
        .module('acmeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('answer-meta-data', {
            parent: 'entity',
            url: '/answer-meta-data',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AnswerMetaData'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/answer-meta-data/answer-meta-data.html',
                    controller: 'AnswerMetaDataController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('answer-meta-data-detail', {
            parent: 'answer-meta-data',
            url: '/answer-meta-data/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'AnswerMetaData'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/answer-meta-data/answer-meta-data-detail.html',
                    controller: 'AnswerMetaDataDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'AnswerMetaData', function($stateParams, AnswerMetaData) {
                    return AnswerMetaData.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'answer-meta-data',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('answer-meta-data-detail.edit', {
            parent: 'answer-meta-data-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answer-meta-data/answer-meta-data-dialog.html',
                    controller: 'AnswerMetaDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AnswerMetaData', function(AnswerMetaData) {
                            return AnswerMetaData.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('answer-meta-data.new', {
            parent: 'answer-meta-data',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answer-meta-data/answer-meta-data-dialog.html',
                    controller: 'AnswerMetaDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                answer: null,
                                associatedQuestion: null,
                                associatedQuestionID: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('answer-meta-data', null, { reload: 'answer-meta-data' });
                }, function() {
                    $state.go('answer-meta-data');
                });
            }]
        })
        .state('answer-meta-data.edit', {
            parent: 'answer-meta-data',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answer-meta-data/answer-meta-data-dialog.html',
                    controller: 'AnswerMetaDataDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AnswerMetaData', function(AnswerMetaData) {
                            return AnswerMetaData.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('answer-meta-data', null, { reload: 'answer-meta-data' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('answer-meta-data.delete', {
            parent: 'answer-meta-data',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/answer-meta-data/answer-meta-data-delete-dialog.html',
                    controller: 'AnswerMetaDataDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AnswerMetaData', function(AnswerMetaData) {
                            return AnswerMetaData.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('answer-meta-data', null, { reload: 'answer-meta-data' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
